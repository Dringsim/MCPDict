#!/usr/bin/env python3

from tables._縣志 import 字表 as 表

class 字表(表):
	key = "cmn_fyd_npgh"
	tones = "44 1 1a 陰平 ꜀,21 2 1b 陽平 ꜁,353 3 2 上 ꜂,,35 5 3 去 ꜄,,2 7 4 入 ꜆"
	_file = "南平官话同音字表*.tsv"
	simplified = 2
