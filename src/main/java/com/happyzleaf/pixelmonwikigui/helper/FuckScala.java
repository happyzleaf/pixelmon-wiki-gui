package com.happyzleaf.pixelmonwikigui.helper;

import java.text.DecimalFormat;

public class FuckScala {
	private static final DecimalFormat FORMATTER = new DecimalFormat();

	static {
		FORMATTER.setMaximumFractionDigits(2);
		FORMATTER.setMinimumFractionDigits(0);
	}

	public static String formatDecimal(double number) {
		return FORMATTER.format(Math.round(number * 100d) / 100d);
	}
}
