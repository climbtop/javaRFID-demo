package com.util;

public class TimeTool {

	static long begin = System.currentTimeMillis();

	public static void begin() {
		begin = System.currentTimeMillis();
	}

	public static void spend() {
		long end = System.currentTimeMillis();
		System.out.println((end - begin) + " ms");
		begin = end;
	}

}
