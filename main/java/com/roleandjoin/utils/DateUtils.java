package com.roleandjoin.utils;

import java.util.Date;

public class DateUtils {
	
	public static long getSecondsPassed(Date date){
		long passed = System.currentTimeMillis() - date.getTime();
		long secondsPassed = passed / 1000;
		return secondsPassed;
	}
}
