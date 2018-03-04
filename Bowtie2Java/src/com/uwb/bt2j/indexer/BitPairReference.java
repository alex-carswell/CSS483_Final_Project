package com.uwb.bt2j.indexer;

public class BitPairReference {
  protected double byteToU32_[];
  protected EList<RefRecord> recs_;
  protected EList<double> cumUnambig_;
  protected EList<double> cumRefOff_;
  protected EList<double> refLens_;
  protected EList<double> refOffs_;
  protected EList<double> refRecOffs_;
  protected byte buf_;
  protected byte sanityBuf_;
  protected double bufSz_;
  protected double bufAllocSz_;
  protected double nrefs_;
  protected boolean loaded_;
  protected boolean sanity_;
  protected boolean useMm_;
  protected boolean useShmem_;
  protected boolean verbose_;  

  public BitPairReference(
		  String in,
			boolean color,
			boolean sanity,
			EList<String> infiles,
			EList<SString<char> > origs,
			boolean infilesSeq,
			boolean useMm,
			boolean useShmem,
			boolean mmSweep,
			boolean verbose,
			boolean startVerbose
		  ) {
    buf_ = null;
    sanityBuf_ = null;
    loaded_ = true;
    sanity_ = sanity;
    useMm_ = useMm;
    useShmem_ = useShmem;
    verbose_ = verbose;
    string s3 = in + ".3." + gEbwt_ext;
	string s4 = in + ".4." + gEbwt_ext;
	
	FILE *f3, *f4;
	if((f3 = fopen(s3.c_str(), "rb")) == NULL) {
	    cerr << "Could not open reference-string index file " << s3 << " for reading." << endl;
		cerr << "This is most likely because your index was built with an older version" << endl
		<< "(<= 0.9.8.1) of bowtie-build.  Please re-run bowtie-build to generate a new" << endl
		<< "index (or download one from the Bowtie website) and try again." << endl;
		loaded_ = false;
		return;
	}
    if((f4 = fopen(s4.c_str(), "rb"))  == NULL) {
        cerr << "Could not open reference-string index file " << s4 << " for reading." << endl;
		loaded_ = false;
		return;
	}
#ifdef BOWTIE_MM
    char *mmFile = NULL;
	if(useMm_) {
		if(verbose_ || startVerbose) {
			cerr << "  Memory-mapping reference index file " << s4.c_str() << ": ";
			logTime(cerr);
		}
		struct stat sbuf;
		if (stat(s4.c_str(), &sbuf) == -1) {
			perror("stat");
			cerr << "Error: Could not stat index file " << s4.c_str() << " prior to memory-mapping" << endl;
			throw 1;
		}
		mmFile = (char*)mmap((void *)0, (double)sbuf.st_size,
				     PROT_READ, MAP_SHARED, fileno(f4), 0);
		if(mmFile == (void *)(-1) || mmFile == NULL) {
			perror("mmap");
			cerr << "Error: Could not memory-map the index file " << s4.c_str() << endl;
			throw 1;
		}
		if(mmSweep) {
			long sum = 0;
			for(off_t i = 0; i < sbuf.st_size; i += 1024) {
				sum += (long) mmFile[i];
			}
			if(startVerbose) {
				cerr << "  Swept the memory-mapped ref index file; checksum: " << sum << ": ";
				logTime(cerr);
			}
		}
	}
#endif
	
	// Read endianness sentinel, set 'swap'
	uint32_t one;
	bool swap = false;
	one = readU<int32_t>(f3, swap);
	if(one != 1) {
		if(useMm_) {
			cerr << "Error: Can't use memory-mapped files when the index is the opposite endianness" << endl;
			throw 1;
		}
		assert_eq(0x1000000, one);
		swap = true; // have to endian swap U32s
	}
	
	// Read # records
	longU sz;
	sz = readU<longU>(f3, swap);
	if(sz == 0) {
		cerr << "Error: number of reference records is 0 in " << s3.c_str() << endl;
		throw 1;
	}
	
	// Read records
	nrefs_ = 0;
	
	// Cumulative count of all unambiguous characters on a per-
	// stretch 8-bit alignment (i.e. count of bytes we need to
	// allocate in buf_)
	longU cumsz = 0;
	longU cumlen = 0;
	// For each unambiguous stretch...
	for(longU i = 0; i < sz; i++) {
		recs_.push_back(RefRecord(f3, swap));
		if(recs_.back().first) {
			// This is the first record for this reference sequence (and the
			// last record for the one before)
			refRecOffs_.push_back((longU)recs_.size()-1);
			// refOffs_ links each reference sequence with the total number of
			// unambiguous characters preceding it in the pasted reference
			refOffs_.push_back(cumsz);
			if(nrefs_ > 0) {
				// refLens_ links each reference sequence with the total number
				// of ambiguous and unambiguous characters in it.
				refLens_.push_back(cumlen);
			}
			cumlen = 0;
			nrefs_++;
		} else if(i == 0) {
			cerr << "First record in reference index file was not marked as "
			     << "'first'" << endl;
			throw 1;
		}
		cumUnambig_.push_back(cumsz);
		cumRefOff_.push_back(cumlen);
		cumsz += recs_.back().len;
		cumlen += recs_.back().off;
		cumlen += recs_.back().len;
	}
	if(verbose_ || startVerbose) {
		cerr << "Read " << nrefs_ << " reference strings from "
		     << sz << " records: ";
		logTime(cerr);
	}
	// Store a cap entry for the end of the last reference seq
	refRecOffs_.push_back((longU)recs_.size());
	refOffs_.push_back(cumsz);
	refLens_.push_back(cumlen);
	cumUnambig_.push_back(cumsz);
	cumRefOff_.push_back(cumlen);
	bufSz_ = cumsz;
	assert_eq(nrefs_, refLens_.size());
	assert_eq(sz, recs_.size());
	if (f3 != NULL) fclose(f3); // done with .3.gEbwt_ext file
	// Round cumsz up to nearest byte boundary
	if((cumsz & 3) != 0) {
		cumsz += (4 - (cumsz & 3));
	}
	bufAllocSz_ = cumsz >> 2;
	assert_eq(0, cumsz & 3); // should be rounded up to nearest 4
	if(useMm_) {
#ifdef BOWTIE_MM
		buf_ = (uint8_t*)mmFile;
		if(sanity_) {
			FILE *ftmp = fopen(s4.c_str(), "rb");
			sanityBuf_ = new uint8_t[cumsz >> 2];
			double ret = fread(sanityBuf_, 1, cumsz >> 2, ftmp);
			if(ret != (cumsz >> 2)) {
				cerr << "Only read " << ret << " bytes (out of " << (cumsz >> 2) << ") from reference index file " << s4.c_str() << endl;
				throw 1;
			}
			fclose(ftmp);
			for(double i = 0; i < (cumsz >> 2); i++) {
				assert_eq(sanityBuf_[i], buf_[i]);
			}
		}
#else
		cerr << "Shouldn't be at " << __FILE__ << ":" << __LINE__ << " without BOWTIE_MM defined" << endl;
		throw 1;
#endif
	} else {
		bool shmemLeader = true;
		if(!useShmem_) {
			// Allocate a buffer to hold the reference string
			try {
				buf_ = new uint8_t[cumsz >> 2];
				if(buf_ == NULL) throw std::bad_alloc();
			} catch(std::bad_alloc& e) {
				cerr << "Error: Ran out of memory allocating space for the bitpacked reference.  Please" << endl
				<< "re-run on a computer with more memory." << endl;
				throw 1;
			}
		} else {
			shmemLeader = ALLOC_SHARED_U8(
										  (s4 + "[ref]"), (cumsz >> 2), &buf_,
										  "ref", (verbose_ || startVerbose));
		}
		if(shmemLeader) {
			// Open the bitpair-encoded reference file
			FILE *f4 = fopen(s4.c_str(), "rb");
			if(f4 == NULL) {
				cerr << "Could not open reference-string index file " << s4.c_str() << " for reading." << endl;
				cerr << "This is most likely because your index was built with an older version" << endl
				<< "(<= 0.9.8.1) of bowtie-build.  Please re-run bowtie-build to generate a new" << endl
				<< "index (or download one from the Bowtie website) and try again." << endl;
				loaded_ = false;
				return;
			}
			// Read the whole thing in
			double ret = fread(buf_, 1, cumsz >> 2, f4);
			// Didn't read all of it?
			if(ret != (cumsz >> 2)) {
				cerr << "Only read " << ret << " bytes (out of " << (cumsz >> 2) << ") from reference index file " << s4.c_str() << endl;
				throw 1;
			}
			// Make sure there's no more
			char c;
			ret = fread(&c, 1, 1, f4);
			assert_eq(0, ret); // should have failed
			fclose(f4);
#ifdef BOWTIE_SHARED_MEM
			if(useShmem_) NOTIFY_SHARED(buf_, (cumsz >> 2));
#endif
		} else {
#ifdef BOWTIE_SHARED_MEM
			if(useShmem_) WAIT_SHARED(buf_, (cumsz >> 2));
#endif
		}
	}
	
	// Populate byteToU32_
	bool big = currentlyBigEndian();
	for(int i = 0; i < 256; i++) {
		uint32_t word = 0;
		if(big) {
			word |= ((i >> 0) & 3) << 24;
			word |= ((i >> 2) & 3) << 16;
			word |= ((i >> 4) & 3) << 8;
			word |= ((i >> 6) & 3) << 0;
		} else {
			word |= ((i >> 0) & 3) << 0;
			word |= ((i >> 2) & 3) << 8;
			word |= ((i >> 4) & 3) << 16;
			word |= ((i >> 6) & 3) << 24;
		}
		byteToU32_[i] = word;
	}
	
#ifndef NDEBUG
	if(sanity_) {
		// Compare the sequence we just read from the compact index
		// file to the true reference sequence.
		EList<SString<char> > *os; // for holding references
		EList<SString<char> > osv(DEBUG_CAT); // for holding ref seqs
		EList<SString<char> > osn(DEBUG_CAT); // for holding ref names
		EList<double> osvLen(DEBUG_CAT); // for holding ref seq lens
		EList<double> osnLen(DEBUG_CAT); // for holding ref name lens
		SStringExpandable<uint32_t> tmp_destU32_;
		if(infiles != NULL) {
			if(infilesSeq) {
				for(double i = 0; i < infiles->size(); i++) {
					// Remove initial backslash; that's almost
					// certainly being used to protect the first
					// character of the sequence from getopts (e.g.,
					// when the first char is -)
					if((*infiles)[i].at(0) == '\\') {
						(*infiles)[i].erase(0, 1);
					}
					osv.push_back(SString<char>((*infiles)[i]));
				}
			} else {
				parseFastas(*infiles, osn, osnLen, osv, osvLen);
			}
			os = &osv;
		} else {
			assert(origs != NULL);
			os = origs;
		}
		
		// Go through the loaded reference files base-by-base and
		// sanity check against what we get by calling getBase and
		// getStretch
		for(double i = 0; i < os->size(); i++) {
			double olen = ((*os)[i]).length();
			double olenU32 = (olen + 12) / 4;
			uint32_t *buf = new uint32_t[olenU32];
			uint8_t *bufadj = (uint8_t*)buf;
			bufadj += getStretch(buf, i, 0, olen, tmp_destU32_);
			for(double j = 0; j < olen; j++) {
				assert_eq((int)(*os)[i][j], (int)bufadj[j]);
				assert_eq((int)(*os)[i][j], (int)getBase(i, j));
			}
			delete[] buf;
		}
	}
#endif
  }
  
  public int getBase(double tidx, double toff) {
	  long reci = refRecOffs_[tidx];   // first record for target reference sequence
		long recf = refRecOffs_[tidx+1]; // last record (exclusive) for target seq
		long bufOff = refOffs_[tidx];
		long off = 0;
		// For all records pertaining to the target reference sequence...
		for(long i = reci; i < recf; i++) {
			off += recs_[i].off;
			if(toff < off) {
				return 4;
			}
			long recOff = off + recs_[i].len;
			if(toff < recOff) {
				toff -= off;
				bufOff += (long)toff;
				long bufElt = (bufOff) >> 2;
				long shift = (bufOff & 3) << 1;
				return ((buf_[bufElt] >> shift) & 3);
			}
			bufOff += recs_[i].len;
			off = recOff;
		} // end for loop over records
		return 4;
  }
  
  public int getStretchNaive(double destU32, double tidx, double toff, double count) {
	  byte dest = (byte)destU32;
		long reci = refRecOffs_[tidx];   // first record for target reference sequence
		long recf = refRecOffs_[tidx+1]; // last record (exclusive) for target seq
		long cur = 0;
		long bufOff = refOffs_[tidx];
		long off = 0;
		// For all records pertaining to the target reference sequence...
		for(long i = reci; i < recf; i++) {
			off += recs_[i].off;
			for(; toff < off && count > 0; toff++) {
				dest[cur++] = 4;
				count--;
			}
			if(count == 0) break;
			if(toff < off + recs_[i].len) {
				bufOff += (long)(toff - off); // move bufOff pointer forward
			} else {
				bufOff += recs_[i].len;
			}
			off += recs_[i].len;
			for(; toff < off && count > 0; toff++) {
				long bufElt = (bufOff) >> 2;
				long shift = (bufOff & 3) << 1;
				dest[cur++] = (buf_[bufElt] >> shift) & 3;
				bufOff++;
				count--;
			}
			if(count == 0) break;
		} // end for loop over records
		// In any chars are left after scanning all the records,
		// they must be ambiguous
		while(count > 0) {
			count--;
			dest[cur++] = 4;
		}
		return 0;
  }
  
  public int getStretch(double destU32,	double tidx,double toff,double count) {
	  if(count == 0) return 0;
		byte dest = (byte)destU32;
		destU32[0] = 0x04040404; // Add Ns, which we might end up using later
		long reci = refRecOffs_[tidx];   // first record for target reference sequence
		long recf = refRecOffs_[tidx+1]; // last record (exclusive) for target seq
		long cur = 4; // keep a cushion of 4 bases at the beginning
		long bufOff = refOffs_[tidx];
		long off = 0;
		long offset = 4;
		boolean firstStretch = true;
		long left  = reci;
		long right = recf;
		long mid   = 0;
		// For all records pertaining to the target reference sequence...
		for(long i = reci; i < recf; i++) {
			if (firstStretch && recf > reci + 16){
				// binary search finds smallest i s.t. toff >= cumRefOff_[i]
				while (left < right-1) {
					mid = left + ((right - left) >> 1);
					if (cumRefOff_[mid] <= toff)
						left = mid;
					else
						right = mid;
				}
				off = cumRefOff_[left];
				bufOff = cumUnambig_[left];
				i = left;
			}
			off += recs_[i].off; // skip Ns at beginning of stretch
			if(toff < off) {
				double cpycnt = min((double)(off - toff), count);
				count -= cpycnt;
				toff += cpycnt;
				cur += cpycnt;
				if(count == 0) break;
			}
			if(toff < off + recs_[i].len) {
				bufOff += toff - off; // move bufOff pointer forward
			} else {
				bufOff += recs_[i].len;
			}
			off += recs_[i].len;
			if(toff < off) {
				if(firstStretch) {
					if(toff + 8 < off && count > 8) {
						// We already added some Ns, so we have to do
						// a fixup at the beginning of the buffer so
						// that we can start clobbering at cur >> 2
						if(cur & 3) {
							offset -= (cur & 3);
						}
						long curU32 = cur >> 2;
						// Do the initial few bases
						if(bufOff & 3) {
							long bufElt = (bufOff) >> 2;
							long low2 = bufOff & 3;
							// Lots of cache misses on the following line
							destU32[curU32] = byteToU32_[buf_[bufElt]];
							for(int j = 0; j < low2; j++) {
								((String)(destU32[curU32]))[j] = 4;
							}
							curU32++;
							offset += low2;
							long chars = 4 - low2;
							count -= chars;
							bufOff += chars;
							toff += chars;
						}
						long bufOffU32 = bufOff >> 2;
						long countLim = count >> 2;
						long offLim = ((off - (toff + 4)) >> 2);
						long lim = min(countLim, offLim);
						// Do the fast thing for as far as possible
						for(long j = 0; j < lim; j++) {
							// Lots of cache misses on the following line
							destU32[curU32] = byteToU32_[buf_[bufOffU32++]];
							curU32++;
						}
						toff += (lim << 2);
						count -= (lim << 2);
						bufOff = bufOffU32 << 2;
						cur = curU32 << 2;
					}
					// Do the slow thing for the rest
					for(; toff < off && count > 0; toff++) {
						long bufElt = (bufOff) >> 2;
						long shift = (bufOff & 3) << 1;
						dest[cur++] = (buf_[bufElt] >> shift) & 3;
						bufOff++;
						count--;
					}
					firstStretch = false;
				} else {
					// Do the slow thing
					for(; toff < off && count > 0; toff++) {
						long bufElt = (bufOff) >> 2;
						long shift = (bufOff & 3) << 1;
						dest[cur++] = (buf_[bufElt] >> shift) & 3;
						bufOff++;
						count--;
					}
				}
			}
			if(count == 0) break;
		} // end for loop over records
		// In any chars are left after scanning all the records,
		// they must be ambiguous
		while(count > 0) {
			count--;
			dest[cur++] = 4;
		}
		return (int)offset;
  }
  
  public final double numRefs() {
	  return nrefs_;
  }
  
  public final double approxLen() {
	  return refLens_[elt];
  }
  
  public final Boolean loaded() {
	  return loaded_;
  }
  
  public final double pastedOffset() {
	  return refOffs_[idx];
  }
  
  public static Pair<double, double> szsFromFasta(EList<FileBuf> is,
			String outfile,
			boolean bigEndian,
			RefReadInParams refparams,
			EList<RefRecord> szs,
			boolean sanity) {
	  RefReadInParams parms = refparams;
		Pair<double,double> sztot;
		if(!outfile.empty()) {
			String file3 = outfile + ".3." + gEbwt_ext;
			String file4 = outfile + ".4." + gEbwt_ext;
			// Open output stream for the '.3.gEbwt_ext' file which will
			// hold the size records.
			FileOutputStream fout3 = new FileOutputStream(file3);
			if(!fout3.good()) {
				System.err.println("Could not open index file for writing: \"" + file3 + "\"" + "\n"
					 + "Please make sure the directory exists and that permissions allow writing by" + "\n"
					 + "Bowtie.");
			}
			BitpairOutFileBuf bpout = new BitpairOutFileBuf(file4);
			// Read in the sizes of all the unambiguous stretches of the genome
			// into a vector of RefRecords.  The input streams are reset once
			// it's done.
			writeU<double>(fout3, 1, bigEndian); // endianness sentinel
			boolean color = parms.color;
			if(color) {
				parms.color = false;
				// Make sure the .3.gEbwt_ext and .4.gEbwt_ext files contain
				// nucleotides; not colors
				long numSeqs = 0;
				parms.color = true;
				writeU<long>(fout3, (long)szs.size(), bigEndian); // write # records
				for(double i = 0; i < szs.size(); i++) {
					szs[i].write(fout3, bigEndian);
				}
				szs.clear();
				// Now read in the colorspace size records; these are
				// the ones that were indexed
				long numSeqs2 = 0;
				sztot = fastaRefReadSizes(is, szs, parms, null, numSeqs2);
			} else {
				long numSeqs = 0;
				sztot = fastaRefReadSizes(is, szs, parms, bpout, numSeqs);
				writeU<long>(fout3, (long)szs.size(), bigEndian); // write # records
				for(double i = 0; i < szs.size(); i++) szs[i].write(fout3, bigEndian);
			}
			if(sztot.first == 0) {
				System.err.println("Error: No unambiguous stretches of characters in the input.  Aborting...");
			}
			bpout.close();
			fout3.close();
		} else {
			// Read in the sizes of all the unambiguous stretches of the
			// genome into a vector of RefRecords
			long numSeqs = 0;
			sztot = fastaRefReadSizes(is, szs, parms, null, numSeqs);
			if(parms.color) {
				parms.color = false;
				EList<RefRecord> szs2(2);
				long numSeqs2 = 0;
				// One less color than base
				parms.color = true;
			}
		}
		return sztot;
  }
}