package com.uwb.bt2j.util;
class IndexTypes {

  public static final String gEbwt_ext;

  //64-bit constants
  public static final long OFF_MASK     = 0xffffffffffffffff;
  public static final long OFF_LEN_MASK = 0xc000000000000000;
  public static final long LS_SIZE      = 0x100000000000000;
  public static final byte OFF_SIZE     = 8;

  public static long TIndexOffU;
  public static long TIndexOff;

  //32-bit constants
  public static final double OFF_MASK     = 0xffffffff;
  public static final double OFF_LEN_MASK = 0xc0000000;
  public static final double LS_SIZE      = 0x10000000;
  public static final byte OFF_SIZE       = 4;
}
