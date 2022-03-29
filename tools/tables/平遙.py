#!/usr/bin/env python3

from tables._縣志 import 字表 as 表

class 字表(表):
	key = "cjy_bz_py"
	_file = "平遥话同音字表*.tsv"
	tones = "22 1 1a 陰平 ꜀,22 2 1b 陽平 ꜁,512 3 2 上 ꜂,,24 5 3 去 ꜄,,22 7 4a 陰入 ꜆,512 8 4b 陽入 ꜇"
