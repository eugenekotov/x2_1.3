package com.kotov.x2.calculator;

import com.kotov.x2.formula.FractionalFormula;
import com.kotov.x2.formula.IndexFormula;
import com.kotov.x2.formula.LetterFormula;
import com.kotov.x2.formula.LetterFormula.Alignment;
import com.kotov.x2.formula.ListFormula;
import com.kotov.x2.formula.PowerFormula;
import com.kotov.x2.formula.SqrtFormula;
import com.kotov.x2.formula.SystemFormula;
import com.kotov.x2.solution.ISolutionItem;
import com.kotov.x2.solution.ISolutionLine;

public final class MathUtils {
	
	private static final String MINUS_NEW = "\u2212";
	private static final String MINUS_OLD = "-";

	private MathUtils() {
	}

	public static long greatestCommonDivisor(long a, long b) {
		while (b != 0) {
			long tmp = a % b;
			a = b;
			b = tmp;
		}
		return a;
	}

	public static String getArgString(long arg) {
		if (arg >= 0) {
			return Long.toString(arg);
		} else {
			StringBuilder result = new StringBuilder();
			result.append("(");
			result.append(arg);
			result.append(")");
			return result.toString();
		}
	}

	public static double round(double value, int accuracy) {
		double multiplier = Math.pow(10, accuracy);
		value = (double) (value * multiplier);
		long tempLong = (long) Math.round(value);
		return (double) (tempLong / multiplier);
	}

	public static ISolutionLine getDFormula() {
		ListFormula result = new ListFormula();
		result.add(new LetterFormula("D="));
		result.add(new PowerFormula(new LetterFormula("b"), new LetterFormula("2")));
		result.add(new LetterFormula("-4ac;"));
		return result;
	}

	public static ISolutionLine getX12Formula() {
		ListFormula result = new ListFormula();
		result.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("1,2")));
		result.add(new LetterFormula("="));
		ListFormula numerator = new ListFormula();
		numerator.add(new LetterFormula("-"));
		numerator.add(new LetterFormula("b?"));
		numerator.add(new SqrtFormula(new LetterFormula("D")));
		LetterFormula denominator = new LetterFormula("2a");
		result.add(new FractionalFormula(numerator, denominator));
		result.add(new LetterFormula(";"));
		return result;
	}
	
	public static int lengthAfterComma(double doubleValue) {
		String afterComma = String.valueOf(doubleValue).split("\\.")[1];
		if (afterComma.length() == 0 || afterComma.equals("0")) {
			return 0;
		}
		return afterComma.length();
	}

	public static ISolutionItem getEquation(double a, double b, double c) {
		ListFormula result = new ListFormula();
		result.add(getPartOfEquation(a, b, c));
		result.add(new LetterFormula("=0;"));
		return result;
	}
	
	public static ISolutionItem getPartOfEquation(double a, double b, double c) {
		ListFormula result = new ListFormula();
		if (a != 0) {
			if (a != 1) {
				result.add(new LetterFormula(getArgDouble(a, false, true)));
			}
			result.add(new PowerFormula(new LetterFormula("x"), new LetterFormula("2")));
		}
		if (b != 0) {
			if (b != 1 || a != 0) { 
				result.add(new LetterFormula(getArgDouble(b, a != 0, true)));
			}
			result.add(new LetterFormula("x"));
		}
		if (c != 0) {
			result.add(new LetterFormula(getArgDouble(c, a != 0 || b != 0, false)));
		}
		if (a == 0 && b == 0 && c == 0) {
			result.add(new LetterFormula("0"));
		}
		return result;
	}

	public static ISolutionItem getEquationInfo(String a, String b, String c) {
		ListFormula result = new ListFormula();
		result.add(new PowerFormula(new LetterFormula(a + "x"), new LetterFormula("2")));
		result.add(new LetterFormula(b + "x"));
		result.add(new LetterFormula(c));
		result.add(new LetterFormula("=0;"));
		return result;
	}


	public static String getArgDouble(double arg, boolean isWithPlus, boolean canEmpty) {
		String result = getSignStr(arg, isWithPlus);
		arg = Math.abs(arg);
		if (arg == 1) {
			return canEmpty ? result : result + "1";
		}
		if (arg == (int) arg) {
			result = result + String.valueOf((long) arg);
		} else {
			result = result + String.valueOf(Math.abs(arg));
		}
		return result;
	}

	public static String getSignStr(double value, boolean isWithPlus) {
		if (isWithPlus) {
			return value >= 0 ? "+" : "-";
		}
		return value >= 0 ? "" : "-";
	}
	
	public static ListFormula getVietaTheory() {
		ListFormula result = new ListFormula();
		result.add(MathUtils.getEquationInfo("", "+p", "+q"));
		result.add(new LetterFormula("         "));
		
		ListFormula equation1Info = new ListFormula();
		equation1Info.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("1")));
		equation1Info.add(new LetterFormula(".", Alignment.CENTER));
		equation1Info.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("2")));
		equation1Info.add(new LetterFormula("=q;"));

		ListFormula equation2Info = new ListFormula();
		equation2Info.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("1")));
		equation2Info.add(new LetterFormula("+"));
		equation2Info.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("2")));
		equation2Info.add(new LetterFormula("=-p"));

		SystemFormula systemEquationInfo = new SystemFormula(equation1Info, equation2Info);
		result.add(systemEquationInfo);
		return result;
	}
	
	public static String changeMinusSymbol(String text) {
		if (android.os.Build.VERSION.SDK_INT > 16) {
			return text.replace(MINUS_OLD, MINUS_NEW);
		}
		return text;
	}
	
	public static String getPointName(double value) {
		value = MathUtils.round(value, 2);
		String result = ((int) value == value) ? String.valueOf((int) value) : String.valueOf(value);
		return MathUtils.changeMinusSymbol(String.valueOf(result));
	}

	public static boolean isTwoEqualZero(double aDouble, double bDouble, double cDouble) {
		return (aDouble == 0 && bDouble == 0) || (aDouble == 0 && cDouble == 0) || (bDouble == 0 && cDouble == 0);
	}
	
}
