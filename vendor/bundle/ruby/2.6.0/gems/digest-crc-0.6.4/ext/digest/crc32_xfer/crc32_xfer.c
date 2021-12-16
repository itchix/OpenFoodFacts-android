/**
 * \file
 * Functions and types for CRC checks.
 *
 * Generated on Sat Feb 29 02:33:18 2020
 * by pycrc v0.9.2, https://pycrc.org
 * using the configuration:
 *  - Width         = 32
 *  - Poly          = 0x000000af
 *  - XorIn         = 0x00000000
 *  - ReflectIn     = False
 *  - XorOut        = 0x00000000
 *  - ReflectOut    = False
 *  - Algorithm     = table-driven
 */

#include "crc32_xfer.h"

/**
 * Static table used for the table_driven implementation.
 */
static const crc32_t crc32_xfer_table[256] = {
	0x00000000, 0x000000af, 0x0000015e, 0x000001f1, 0x000002bc, 0x00000213, 0x000003e2, 0x0000034d,
	0x00000578, 0x000005d7, 0x00000426, 0x00000489, 0x000007c4, 0x0000076b, 0x0000069a, 0x00000635,
	0x00000af0, 0x00000a5f, 0x00000bae, 0x00000b01, 0x0000084c, 0x000008e3, 0x00000912, 0x000009bd,
	0x00000f88, 0x00000f27, 0x00000ed6, 0x00000e79, 0x00000d34, 0x00000d9b, 0x00000c6a, 0x00000cc5,
	0x000015e0, 0x0000154f, 0x000014be, 0x00001411, 0x0000175c, 0x000017f3, 0x00001602, 0x000016ad,
	0x00001098, 0x00001037, 0x000011c6, 0x00001169, 0x00001224, 0x0000128b, 0x0000137a, 0x000013d5,
	0x00001f10, 0x00001fbf, 0x00001e4e, 0x00001ee1, 0x00001dac, 0x00001d03, 0x00001cf2, 0x00001c5d,
	0x00001a68, 0x00001ac7, 0x00001b36, 0x00001b99, 0x000018d4, 0x0000187b, 0x0000198a, 0x00001925,
	0x00002bc0, 0x00002b6f, 0x00002a9e, 0x00002a31, 0x0000297c, 0x000029d3, 0x00002822, 0x0000288d,
	0x00002eb8, 0x00002e17, 0x00002fe6, 0x00002f49, 0x00002c04, 0x00002cab, 0x00002d5a, 0x00002df5,
	0x00002130, 0x0000219f, 0x0000206e, 0x000020c1, 0x0000238c, 0x00002323, 0x000022d2, 0x0000227d,
	0x00002448, 0x000024e7, 0x00002516, 0x000025b9, 0x000026f4, 0x0000265b, 0x000027aa, 0x00002705,
	0x00003e20, 0x00003e8f, 0x00003f7e, 0x00003fd1, 0x00003c9c, 0x00003c33, 0x00003dc2, 0x00003d6d,
	0x00003b58, 0x00003bf7, 0x00003a06, 0x00003aa9, 0x000039e4, 0x0000394b, 0x000038ba, 0x00003815,
	0x000034d0, 0x0000347f, 0x0000358e, 0x00003521, 0x0000366c, 0x000036c3, 0x00003732, 0x0000379d,
	0x000031a8, 0x00003107, 0x000030f6, 0x00003059, 0x00003314, 0x000033bb, 0x0000324a, 0x000032e5,
	0x00005780, 0x0000572f, 0x000056de, 0x00005671, 0x0000553c, 0x00005593, 0x00005462, 0x000054cd,
	0x000052f8, 0x00005257, 0x000053a6, 0x00005309, 0x00005044, 0x000050eb, 0x0000511a, 0x000051b5,
	0x00005d70, 0x00005ddf, 0x00005c2e, 0x00005c81, 0x00005fcc, 0x00005f63, 0x00005e92, 0x00005e3d,
	0x00005808, 0x000058a7, 0x00005956, 0x000059f9, 0x00005ab4, 0x00005a1b, 0x00005bea, 0x00005b45,
	0x00004260, 0x000042cf, 0x0000433e, 0x00004391, 0x000040dc, 0x00004073, 0x00004182, 0x0000412d,
	0x00004718, 0x000047b7, 0x00004646, 0x000046e9, 0x000045a4, 0x0000450b, 0x000044fa, 0x00004455,
	0x00004890, 0x0000483f, 0x000049ce, 0x00004961, 0x00004a2c, 0x00004a83, 0x00004b72, 0x00004bdd,
	0x00004de8, 0x00004d47, 0x00004cb6, 0x00004c19, 0x00004f54, 0x00004ffb, 0x00004e0a, 0x00004ea5,
	0x00007c40, 0x00007cef, 0x00007d1e, 0x00007db1, 0x00007efc, 0x00007e53, 0x00007fa2, 0x00007f0d,
	0x00007938, 0x00007997, 0x00007866, 0x000078c9, 0x00007b84, 0x00007b2b, 0x00007ada, 0x00007a75,
	0x000076b0, 0x0000761f, 0x000077ee, 0x00007741, 0x0000740c, 0x000074a3, 0x00007552, 0x000075fd,
	0x000073c8, 0x00007367, 0x00007296, 0x00007239, 0x00007174, 0x000071db, 0x0000702a, 0x00007085,
	0x000069a0, 0x0000690f, 0x000068fe, 0x00006851, 0x00006b1c, 0x00006bb3, 0x00006a42, 0x00006aed,
	0x00006cd8, 0x00006c77, 0x00006d86, 0x00006d29, 0x00006e64, 0x00006ecb, 0x00006f3a, 0x00006f95,
	0x00006350, 0x000063ff, 0x0000620e, 0x000062a1, 0x000061ec, 0x00006143, 0x000060b2, 0x0000601d,
	0x00006628, 0x00006687, 0x00006776, 0x000067d9, 0x00006494, 0x0000643b, 0x000065ca, 0x00006565
};

crc32_t crc32_xfer_update(crc32_t crc, const void *data, size_t data_len)
{
	const unsigned char *d = (const unsigned char *)data;
	unsigned int tbl_idx;

	while (data_len--)
	{
		tbl_idx = ((crc >> 24) ^ *d) & 0xff;
		crc = (crc32_xfer_table[tbl_idx] ^ (crc << 8)) & 0xffffffff;
		d++;
	}

	return crc & 0xffffffff;
}
