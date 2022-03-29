#!/usr/bin/env python3

from tables._縣志 import 字表 as 表

class 字表(表):
	key = "czh_qw_fljc"
	_file = "浮梁舊城同音字彙.tsv"
	tones = "55 1 1a 陰平 ꜀,24 2 1b 陽平 ꜁,21 3 2 上 ꜂,,213 5 3a 陰去 ꜄,33 6 3b 陽去 ꜅"
	simplified = 2
