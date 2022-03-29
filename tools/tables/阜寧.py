#!/usr/bin/env python3

from tables._縣志 import 字表 as 表

class 字表(表):
	key = "cmn_jh_hc_fn"
	note = "說明：阜寧包含清朝以來老阜寧縣範圍，包含今阜寧、濱海全部，射陽黃沙港以北，漣水、響水、淮安、建湖部分地區。發音以老派爲準。"
	tones = "52 1 1a 陰平 ꜀,25 2 1b 陽平 ꜁,211 3 2 上 ꜂,,334 5 3 去 ꜄,,4 7 4 入 ꜆"
	_file = "阜寧同音字表*.tsv"
