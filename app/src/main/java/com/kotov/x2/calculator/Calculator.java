package com.kotov.x2.calculator;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.kotov.x2.R;
import com.kotov.x2.formula.FractionalFormula;
import com.kotov.x2.formula.Graph;
import com.kotov.x2.formula.IndexFormula;
import com.kotov.x2.formula.LetterFormula;
import com.kotov.x2.formula.LetterFormula.Alignment;
import com.kotov.x2.formula.ListFormula;
import com.kotov.x2.formula.PowerFormula;
import com.kotov.x2.formula.SqrtFormula;
import com.kotov.x2.formula.SystemFormula;
import com.kotov.x2.formula.TextPart;
import com.kotov.x2.formula.TransferPart;
import com.kotov.x2.solution.ISolutionItem;
import com.kotov.x2.solution.Solution;

public class Calculator {

	public enum X {
		X1(1, "+", 1), X2(2, "-", -1);

		private int index;
		private String sign;
		private int signum;

		private X(int index, String sign, int signum) {
			this.index = index;
			this.sign = sign;
			this.signum = signum;
		}

		public int getIndex() {
			return index;
		}

		public String getSign() {
			return sign;
		}

		public int getSignum() {
			return signum;
		}
	}

	private Context context;

	private double aDouble;
	private double bDouble;
	private double cDouble;

	private long aLong;
	private long bLong;
	private long cLong;
	private long d;
	private double x1Double;
	private double x2Double;
	private long x1Long;
	private long x2Long;

	private long dIntPart = 0; // an entire part of the root of the discriminant
	private long dSqrtPart = 0; // under the root of the discriminant

	private ListFormula equation1;
	private ListFormula equation2;
	private Solution solution;

	public Calculator(Context context, double a, double b, double c) {
		this.context = context;
		this.aDouble = a;
		this.bDouble = b;
		this.cDouble = c;
		calc();
	}

	private void calc() {
		solution = new Solution();
		if (!calcABC()) {
			return;
		}
		if (aLong == 0 || bLong == 0 || cLong == 0) {
			calcIncompleteEquation();
		} else {
			calcD();
			if (d < 0) {
				solution.add(new TextPart(context.getString(R.string.message_no_solution)));
			} else {
				calcSqrtD();
				calcX();
				calcVieta();
			}
		}
		if (aDouble != 0 || bDouble != 0) {
			calcGraph();
		}
	}

	private void calcIncompleteEquation() {
		if (aLong == 0 && bLong == 0 && cLong == 0) {
			// a,b,c == 0
			solution.add(new TextPart(context.getString(R.string.solution_incomplete_text_1)));
		} else if (aLong == 0 && bLong == 0 && cLong != 0) {
			// a,b == 0, c != 0
			solution.add(new TextPart(context.getString(R.string.solution_incomplete_text_2)));
			d = -1;
		} else if (aLong == 0) {
			// a == 0
			if (cLong != 0) {
				// a == 0, b != 0, c != 0
				x1Double = calcLinearEquation(bLong, cLong);
			} else {
				// a == 0, b != 0, c == 0
				if (bLong != 1) {
					solution.add(new LetterFormula(context.getString(R.string.solution_incomplete_text_3)));
				}
				x1Double = 0;
			}
			x2Double = x1Double;
			d = 0;
		} else if (aLong != 0 && bLong != 0) {
			// a != 0, b != 0, c == 0
			StringBuilder resultString = new StringBuilder();
			resultString.append("x(");
			if (aLong != 1) {
				resultString.append(MathUtils.getArgDouble(aLong, false, true));
			}
			resultString.append("x");
			resultString.append(MathUtils.getArgDouble(bLong, true, false));
			resultString.append(")=0;");

			solution.add(new LetterFormula(resultString.toString()));
			solution.add(new TextPart(context.getString(R.string.solution_incomplete_text_4) + MathUtils.getArgDouble(aLong, false, true) + "x"
					+ MathUtils.getArgDouble(bLong, true, false) + "=0;"));
			solution.add(new LetterFormula(context.getString(R.string.solution_incomplete_text_5)));
			solution.add(new LetterFormula(MathUtils.getArgDouble(aLong, false, true) + "x"
					+ MathUtils.getArgDouble(bLong, true, false) + "=0;"));
			double equationResult = calcLinearEquation(aLong, bLong);
			ListFormula result = new ListFormula();
			result.add(new LetterFormula(context.getString(R.string.solution_incomplete_text_6)));
			result.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("1")));
			result.add(new LetterFormula("=0,  "));
			result.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("2")));
			result.add(getCalculationFormulaResult(equationResult, null));
			solution.add(result);
			x1Double = 0;
			x2Double = equationResult;
			d = 1;
		} else {
			if (cLong != 0) {
				// a != 0, b == 0, c != 0
				double resultSquared = ((double) -cLong) / ((double) aLong);
				ListFormula result = new ListFormula();
				result.add(new LetterFormula(MathUtils.getArgDouble(aLong, false, true)));
				result.add(new PowerFormula(new LetterFormula("x"), new LetterFormula("2")));
				result.add(new LetterFormula("="));
				result.add(new LetterFormula(MathUtils.getArgDouble(-cLong, false, false)));
				result.add(new LetterFormula(";"));
				solution.add(result);
				if (Math.abs(aLong) != 1) {
					result = new ListFormula();
					result.add(new PowerFormula(new LetterFormula("x"), new LetterFormula("2")));
					result.add(new LetterFormula("="));
					if (resultSquared < 0) {
						result.add(new LetterFormula("-"));
					}
					result.add(new FractionalFormula(new LetterFormula(MathUtils.getArgDouble(Math.abs(cLong), false,
							false)), new LetterFormula(MathUtils.getArgDouble(Math.abs(aLong), false, false))));
					result.add(new LetterFormula(";"));
					solution.add(result);
				} else {
					if (aLong == -1) {
						result = new ListFormula();
						result.add(new PowerFormula(new LetterFormula("x"), new LetterFormula("2")));
						result.add(new LetterFormula("="));
						result.add(new LetterFormula(MathUtils.getArgDouble(resultSquared, false, false)));
						result.add(new LetterFormula(";"));
						solution.add(result);
					}
				}
				if (resultSquared < 0) {
					solution.add(new TextPart(context.getString(R.string.solution_incomplete_text_2)));
					d = -1;
				} else {
					result = new ListFormula();
					result.add(new LetterFormula("x=±"));
					if (Math.abs(aLong) == 1) {
						result.add(new SqrtFormula(new LetterFormula(MathUtils
								.getArgDouble(resultSquared, false, false))));
						Sqrt sqrt = new Sqrt((long) (resultSquared));
						if (sqrt.isReduced()) {
							result.add(new LetterFormula("="));
							result.add(sqrt.getFormula(true));
							if (sqrt.getSqrtPart() != 1) {
								result.add(getCalculationFormulaResult(Math.sqrt(resultSquared), "±"));
							} else {
								result.add(new LetterFormula(";"));
							}
						} else {
							result.add(getCalculationFormulaResult(Math.sqrt(resultSquared), "±"));
						}
					} else {
						result.add(new SqrtFormula(new FractionalFormula(new LetterFormula(MathUtils.getArgDouble(
								Math.abs(cLong), false, false)), new LetterFormula(MathUtils.getArgDouble(
								Math.abs(aLong), false, false)))));
						Sqrt sqrtNumerator = new Sqrt(Math.abs(cLong));
						Sqrt sqrtDenominator = new Sqrt(Math.abs(aLong));
						if (sqrtNumerator.isReduced() || sqrtDenominator.isReduced()) {
							result.add(new LetterFormula("=±"));
							result.add(new FractionalFormula(sqrtNumerator.getFormula(false), sqrtDenominator
									.getFormula(false)));
						}
						result.add(getCalculationFormulaResult(Math.sqrt(resultSquared), "±"));
					}
					solution.add(result);
					result = new ListFormula();
					result.add(new LetterFormula(context.getString(R.string.solution_incomplete_text_6)));
					result.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("1")));
					result.add(getCalculationFormulaResult(Math.sqrt(resultSquared), "-"));
					result.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("2")));
					result.add(getCalculationFormulaResult(Math.sqrt(resultSquared), null));
					solution.add(result);
					x1Double = -Math.sqrt(resultSquared);
					x2Double = Math.sqrt(resultSquared);
					d = 1;
				}
			} else {
				// a != 0, b == 0, c == 0
				solution.add(new LetterFormula("x=0;"));
				x1Double = 0;
				x2Double = 0;
				d = 0;
			}
		}
	}

	private double calcLinearEquation(long a, long b) {
		ListFormula result = new ListFormula();
		result.add(new LetterFormula(MathUtils.getArgDouble(a, false, true)));
		result.add(new LetterFormula("x="));
		result.add(new LetterFormula(MathUtils.getArgDouble(-b, false, false)));
		result.add(new LetterFormula(";"));
		solution.add(result);
		double equationResult = ((double) -b) / ((double) a);
		if (Math.abs(a) == 1) {
			if (a == -1) {
				result = new ListFormula();
				result.add(new LetterFormula("x"));
				result.add(getCalculationFormulaResult(equationResult, null));
				solution.add(result);
			}
		} else {
			result = new ListFormula();
			result.add(new LetterFormula("x="));
			if (equationResult < 0) {
				result.add(new LetterFormula("-"));
			}
			result.add(new FractionalFormula(new LetterFormula(MathUtils.getArgDouble(Math.abs(b), false, false)),
					new LetterFormula(MathUtils.getArgDouble(Math.abs(a), false, false))));
			Fraction fraction = new Fraction(Math.abs(-b), Math.abs(a));
			if (fraction.isRedused()) {
				result.add(new LetterFormula("="));
				if (equationResult < 0) {
					result.add(new LetterFormula("-"));
				}
				result.add(fraction.getFormula());
			}
			result.add(getCalculationFormulaResult(equationResult, null));
			solution.add(result);
		}
		return equationResult;
	}

	private boolean calcABC() {
		// Check if we can simplify
		long multiplier = (long) Math.pow(
				10,
				Math.max(MathUtils.lengthAfterComma(aDouble),
						Math.max(MathUtils.lengthAfterComma(bDouble), MathUtils.lengthAfterComma(cDouble))));
		aLong = (long) (aDouble * multiplier);
		bLong = (long) (bDouble * multiplier);
		cLong = (long) (cDouble * multiplier);
		if (MathUtils.isTwoEqualZero(aDouble, bDouble, cDouble)) {
			solution.add(getOriginEquation());
			return true;
		}
		// Spread on the factors and determine the greatest common divisor
		List<Long> factorsA = getFactors(Math.abs(aLong));
		List<Long> factorsB = getFactors(Math.abs(bLong));
		List<Long> factorsC = getFactors(Math.abs(cLong));
		List<Long> factors;
		if (aLong != 0) {
			factors = factorsA;
		} else if (bLong != 0) {
			factors = factorsB;
		} else if (cLong != 0) {
			factors = factorsC;
		} else {
			factors = new ArrayList<Long>();
		}
		long divider = 1;
		for (int i = factors.size() - 1; i >= 0; i--) {
			Long factor = factors.get(i);
			if ((factorsA.contains(factor) || aLong == 0) && (factorsB.contains(factor) || bLong == 0)
					&& (factorsC.contains(factor) || cLong == 0)) {
				factorsA.remove(factor);
				factorsB.remove(factor);
				factorsC.remove(factor);
				divider = divider * factor;
			}
		}
		aLong = (long) (aLong / divider);
		bLong = (long) (bLong / divider);
		cLong = (long) (cLong / divider);
		boolean overflow = (aLong > 99999999 || bLong > 99999999 || cLong > 99999999);
		if (overflow) {
			solution.add(new TextPart(context.getString(R.string.message_overflow)));
		} else {
			if (multiplier != 1 || divider != 1) {
				solution.add(getOriginEquation());
				StringBuilder message = new StringBuilder(context.getString(R.string.message_simplification));
				if (multiplier != 1) {
					message.append(context.getString(R.string.message_simplification_1));
					message.append(multiplier);
				}
				if (divider != 1) {
					message.append(context.getString(R.string.message_simplification_2));
					message.append(divider);
				}
				message.append(context.getString(R.string.message_simplification_3));
				solution.add(new TextPart(message.toString()));
			}
			solution.add(getSimplifiedEquation());
		}
		return !overflow;
	}

	private void calcX() {
		x1Double = (-bLong + Math.sqrt(d)) / (2 * aLong);
		x2Double = (-bLong - Math.sqrt(d)) / (2 * aLong);
		//
		solution.add(MathUtils.getX12Formula());
		solution.add(getXCalculationFormulas(X.X1));
		if (d > 0) {
			solution.add(getXCalculationFormulas(X.X2));
		}
	}

	private void calcD() {
		d = bLong * bLong - 4 * aLong * cLong;
		createDCalculationFormulas();
	}

	private void createDCalculationFormulas() {
		List<ISolutionItem> dCalculationFormulasList = new ArrayList<ISolutionItem>();
		//
		ListFormula step = new ListFormula();
		step.add(new LetterFormula("D"));
		dCalculationFormulasList.add(step);
		// supply figures
		step = new ListFormula();
		step.add(new PowerFormula(new LetterFormula(MathUtils.getArgString(bLong)), new LetterFormula("2")));
		step.add(new LetterFormula("-4"));
		step.add(new LetterFormula(".", Alignment.CENTER));
		step.add(new LetterFormula(MathUtils.getArgString(aLong)));
		step.add(new LetterFormula(".", Alignment.CENTER));
		step.add(new LetterFormula(MathUtils.getArgString(cLong)));

		dCalculationFormulasList.add(step);
		// interim counting
		step = new ListFormula();
		step.add(new LetterFormula(MathUtils.getArgString(bLong * bLong)));
		step.add(new LetterFormula(MathUtils.getSignStr(-4 * aLong * cLong, true)));
		step.add(new LetterFormula(Long.toString(Math.abs(4 * aLong * cLong))));
		dCalculationFormulasList.add(step);
		// result
		dCalculationFormulasList.add(new LetterFormula(Long.toString(d) + ";"));
		//
		solution.add(new TextPart(context.getString(R.string.solution_descriminant_title), true));
		solution.add(getABCFormula());
		solution.add(MathUtils.getDFormula());
		solution.add(new TransferPart(dCalculationFormulasList));
	}

	private void calcSqrtD() {
		Sqrt sqrt = new Sqrt(d);
		dIntPart = (long) sqrt.getIntPart();
		dSqrtPart = (long) sqrt.getSqrtPart();
	}

	private ISolutionItem getSqrtDCalculationFormula() {
		return getSqrtDCalculationFormula(dIntPart, dSqrtPart);
	}

	private ISolutionItem getSqrtDCalculationFormula(long intPart, long sqrtPart) {
		if (sqrtPart == 1) {
			return new LetterFormula(Long.toString(intPart));
		}

		ListFormula result = new ListFormula();
		if (intPart != 1) {
			result.add(new LetterFormula(Long.toString(intPart)));
		}
		result.add(new SqrtFormula(new LetterFormula(Long.toString(sqrtPart))));
		return result;
	}

	private List<Long> getFactors(long n) {
		List<Long> result = new ArrayList<Long>();
		long factor = 2;
		while (n > 1 && factor * factor <= n) {
			while (n % factor == 0) {
				result.add(factor);
				n = n / factor;
			}
			factor++;
		}
		if (n > 1 || result.isEmpty()) {
			result.add(n);
		}
		return result;
	}

	private class Pair {

		long n1;
		long n2;

		public Pair(long n1, long n2) {
			this.n1 = n1;
			this.n2 = n2;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof Pair)) {
				return false;
			}
			Pair secondPair = (Pair) o;
			return (this.n1 == secondPair.n1 && this.n2 == secondPair.n2)
					|| (this.n1 == secondPair.n2 && this.n2 == secondPair.n1);
		}

		@Override
		public String toString() {
			return "(" + n1 + ";" + n2 + ")";
		}
	}

	private enum XsInvetigation {
		BOTH_POSITIVE, BOTH_NEGATIVE, LAGER_POSITIVE, LAGER_NEGATIVE;
	}

	/*
	 * Returns pair of numbers that are in the multiplication give our number
	 */
	private List<Pair> getPairs(long value, XsInvetigation xsInvetigation) {
		List<Pair> result = new ArrayList<Pair>();
		for (long i = 1; i <= Math.sqrt(value); i++) {
			if (value % i == 0) {
				Pair pair = setPairSign(new Pair(value / i, i), xsInvetigation);
				if (!result.contains(pair)) {
					result.add(pair);
				}
			}
		}
		return result;
	}

	private Pair setPairSign(Pair pair, XsInvetigation xsInvetigation) {
		pair.n1 = Math.abs(pair.n1);
		pair.n2 = Math.abs(pair.n2);
		if (xsInvetigation == XsInvetigation.BOTH_NEGATIVE) {
			pair.n1 = -pair.n1;
			pair.n2 = -pair.n2;
		} else if (xsInvetigation == XsInvetigation.LAGER_NEGATIVE) {
			if (pair.n1 > pair.n2) {
				pair.n1 = -pair.n1;
			} else {
				pair.n2 = -pair.n2;
			}
		} else if (xsInvetigation == XsInvetigation.LAGER_POSITIVE) {
			if (pair.n1 > pair.n2) {
				pair.n2 = -pair.n2;
			} else {
				pair.n1 = -pair.n1;
			}
		}
		return pair;
	}

	private ISolutionItem getOriginEquation() {
		return MathUtils.getEquation(aDouble, bDouble, cDouble);
	}

	private ISolutionItem getSimplifiedEquation() {
		return MathUtils.getEquation(aLong, bLong, cLong);
	}

	private ISolutionItem getABCFormula() {
		StringBuffer text = new StringBuffer();
		text.append("a=");
		text.append(aLong);
		text.append(", b=");
		text.append(bLong);
		text.append(", c=");
		text.append(cLong);
		text.append(";");
		return new LetterFormula(text.toString());
	}

	private TransferPart getXCalculationFormulas(X x) {
		List<ISolutionItem> result = new ArrayList<ISolutionItem>();

		ListFormula step = new ListFormula();
		if (d == 0) {
			step.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("1,2")));
		} else {
			step.add(new IndexFormula(new LetterFormula("x"), new LetterFormula(Long.toString(x.getIndex()))));
		}
		result.add(step);
		// substitute numbers
		step = new ListFormula();
		ListFormula numerator = new ListFormula();
		numerator.add(new LetterFormula("-"));
		numerator.add(new LetterFormula(MathUtils.getArgString(bLong)));
		if (d == 0) {
			numerator.add(new LetterFormula("±"));
		} else {
			numerator.add(new LetterFormula(x.getSign()));
		}
		numerator.add(new SqrtFormula(new LetterFormula(MathUtils.getArgString(d))));
		ListFormula denominator = new ListFormula();
		denominator.add(new LetterFormula("2"));
		denominator.add(new LetterFormula(".", Alignment.CENTER));
		denominator.add(new LetterFormula(MathUtils.getArgString(aLong)));
		step.add(new FractionalFormula(numerator, denominator));
		result.add(step);
		// calculate -b, D, 2a, if out of the root can be taken either an
		// integer or a root of 1
		if (dIntPart != 1 || dSqrtPart == 1 || d == 0) {
			step = new ListFormula();
			numerator = new ListFormula();
			numerator.add(new LetterFormula(Long.toString(-bLong)));
			if (d != 0) {
				numerator.add(new LetterFormula(x.getSign()));
				numerator.add(getSqrtDCalculationFormula());
			}
			step.add(new FractionalFormula(numerator, new LetterFormula(Long.toString(2 * aLong))));
			result.add(step);
		}
		//
		if (d == 0) {
			result.addAll(getXCalculationFormulaD0());
		} else {
			if (dSqrtPart == 1) {
				result.addAll(getXCalculationFormulaIntD(x));
			} else {
				result.addAll(getXCalculationFormulaSqrtD(x));
			}
		}
		return new TransferPart(result);
	}

	/*
	 * Return formula when D=0
	 */
	private List<ISolutionItem> getXCalculationFormulaD0() {
		return getCalculationFraction(-bLong, 2 * aLong);
	}

	private List<ISolutionItem> getCalculationFraction(long numerator, long denominator) {

		List<ISolutionItem> result = new ArrayList<ISolutionItem>();

		Fraction fraction = new Fraction(Math.abs(numerator), Math.abs(denominator));

		int sign = Long.signum(numerator) * Long.signum(denominator);
		if (sign < 0 || fraction.isRedused()) {
			ListFormula step = new ListFormula();
			if (sign < 0) {
				step.add(new LetterFormula("-"));
			}
			step.add(fraction.getFormula());
			if (fraction.getDenominator() == 1) {
				step.add(new LetterFormula(";"));
			}
			result.add(step);
		}
		// calculate the approximate value
		if (fraction.getDenominator() != 1) {
			result.addAll(getCalculationFormulaResult(sign * fraction.getFloatValue(), null));
		}
		return result;
	}

	private List<ISolutionItem> getCalculationFormulaResult(double x, String signString) {
		List<ISolutionItem> result = new ArrayList<ISolutionItem>();
		double xRounded = MathUtils.round(x, 3);
		if (x == xRounded) {
			result.add(new LetterFormula("="));
			if (signString != null && !signString.isEmpty()) {
				result.add(new LetterFormula(signString));
			}
			xRounded = MathUtils.round(x, 0);
			if (x == xRounded) {
				result.add(new LetterFormula(Long.toString((long) x) + ";"));
			} else {
				result.add(new LetterFormula(Double.toString(x) + ";"));
			}
		} else {
			result.add(new LetterFormula("\u2248"));
			if (signString != null && !signString.isEmpty()) {
				result.add(new LetterFormula(signString));
			}
			result.add(new LetterFormula(Double.toString(MathUtils.round(x, 3)) + ";"));
		}
		return result;
	}

	/*
	 * solution for fractional root of the discriminant
	 */
	private List<ISolutionItem> getXCalculationFormulaSqrtD(X x) {

		List<ISolutionItem> result = new ArrayList<ISolutionItem>();

		ListFormula step = new ListFormula();
		// divided into two fractions
		// first fraction
		if (Long.signum(-bLong) * Long.signum(2 * aLong) < 0) {
			step.add(new LetterFormula("-"));
		}
		Fraction fraction1 = new Fraction(Math.abs(-bLong), Math.abs(2 * aLong));
		step.add(fraction1.getFormula());
		// second fraction
		FractionSqrt fraction2 = new FractionSqrt(Math.abs(dIntPart), dSqrtPart, Math.abs(2 * aLong));
		step.add(new LetterFormula(MathUtils.getSignStr(Long.signum(dIntPart) * Long.signum(2 * aLong) * x.getSignum(),
				true)));
		step.add(fraction2.getFormula());

		result.add(step);

		// second stage, reducing fractions
		if (fraction1.isRedused() | fraction2.isRedused()) {
			step = new ListFormula();
			// first fraction
			if (Long.signum(-bLong) * Long.signum(2 * aLong) < 0) {
				step.add(new LetterFormula("-"));
			}
			step.add(fraction1.getFormula());
			// second fraction
			step.add(new LetterFormula(MathUtils.getSignStr(
					Long.signum(dIntPart) * Long.signum(2 * aLong) * x.getSignum(), true)));
			step.add(fraction2.getFormula());
			result.add(step);
		}
		// third stage, we calculate the approximate value
		double answer = (-bLong + dIntPart * Math.sqrt(dSqrtPart) * x.getSignum()) / (2 * aLong);
		result.addAll(getCalculationFormulaResult(answer, null));
		return result;
	}

	/*
	 * decision in the whole root of the discriminant
	 */
	private List<ISolutionItem> getXCalculationFormulaIntD(X x) {
		List<ISolutionItem> result = new ArrayList<ISolutionItem>();
		long numerator;
		if (x.getSign().equals("+")) {
			numerator = -bLong + dIntPart;
		} else {
			numerator = -bLong - dIntPart;
		}
		long denominator = 2 * aLong;
		result.add(new FractionalFormula(new LetterFormula(Long.toString(numerator)), new LetterFormula(Long
				.toString(denominator))));
		result.addAll(getCalculationFraction(numerator, denominator));
		return result;
	}

	class Fraction {

		private long intPart = 0;
		private long numerator;
		private long denominator;
		private boolean redused = false;

		public Fraction(long numerator, long denominator) {
			this.numerator = numerator;
			this.denominator = denominator;
			redused = false;
			long greatestCommonDivisor = MathUtils.greatestCommonDivisor(this.numerator, this.denominator);
			if (greatestCommonDivisor != 1) {
				redused = true;
				this.numerator = this.numerator / greatestCommonDivisor;
				this.denominator = this.denominator / greatestCommonDivisor;
			}
			redused = redused | calcIntPart();
		}

		private boolean calcIntPart() {
			if (Math.abs(this.numerator) < Math.abs(this.denominator)) {
				return false;
			}
			this.intPart = this.numerator / this.denominator;
			this.numerator = Math.abs(this.numerator % this.denominator);
			this.denominator = Math.abs(this.denominator);
			return true;
		}

		public ISolutionItem getFormula() {
			if (this.denominator == 1) {
				return new LetterFormula(Long.toString(this.intPart));
			}
			if (this.intPart == 0) {
				return getFractionalFormula();
			} else {
				ListFormula result = new ListFormula();
				result.add(new LetterFormula(MathUtils.getArgDouble(this.intPart, false, false)));
				result.add(getFractionalFormula());
				return result;
			}
		}

		private ISolutionItem getFractionalFormula() {
			return new FractionalFormula(new LetterFormula(Long.toString(this.numerator)), new LetterFormula(
					Long.toString(this.denominator)));
		}

		public double getFloatValue() {
			return this.intPart + (double) this.numerator / (double) this.denominator;
		}

		public boolean isRedused() {
			return redused;
		}

		public long getIntPart() {
			return intPart;
		}

		public long getNumerator() {
			return numerator;
		}

		public long getDenominator() {
			return denominator;
		}

	}

	class Sqrt {

		private long arg;
		private long intPart = 1;
		private long sqrtPart = 1;
		private boolean redused = false;

		public Sqrt(long arg) {
			this.arg = arg;
			double sqrt = Math.sqrt(arg);
			if (sqrt == (long) sqrt) {
				intPart = (long) sqrt;
				sqrtPart = 1;
				redused = true;
			} else {
				calc();
				redused = (sqrtPart != arg);
			}
		}

		public ISolutionItem getFormula(boolean isWithPlusMinus) {
			if (sqrtPart == 1) {
				return new LetterFormula((isWithPlusMinus ? "±" : "") + MathUtils.getArgDouble(intPart, false, false));
			} else if (intPart == 1) {
				return new SqrtFormula(new LetterFormula(MathUtils.getArgDouble(arg, false, false)));
			}
			ListFormula result = new ListFormula();
			result.add(new LetterFormula((isWithPlusMinus ? "±" : "") + MathUtils.getArgDouble(intPart, false, false)));
			result.add(new SqrtFormula(new LetterFormula(MathUtils.getArgDouble(sqrtPart, false, false))));
			return result;
		}

		private void calc() {
			List<Long> factors = getFactors(arg);
			int i = 0;
			while (i < factors.size() - 1) {
				if (factors.get(i) == factors.get(i + 1)) {
					intPart = intPart * factors.get(i);
					i = i + 2;
				} else {
					sqrtPart = sqrtPart * factors.get(i);
					i++;
				}
			}
			if (i == factors.size() - 1) {
				sqrtPart = sqrtPart * factors.get(factors.size() - 1);
			}
		}

		public boolean isReduced() {
			return redused;
		}

		public double getIntPart() {
			return intPart;
		}

		public double getSqrtPart() {
			return sqrtPart;
		}
	}

	class FractionSqrt {

		private long numeratorInt;
		private long numeratorSqrt;
		private long denominator;
		private boolean redused = false;

		private FractionSqrt(long numeratorInt, long numeratorSqrt, long denominator) {
			this.numeratorInt = numeratorInt;
			this.numeratorSqrt = numeratorSqrt;
			this.denominator = denominator;
			redused = false;
			long greatestCommonDivisor = MathUtils.greatestCommonDivisor(this.numeratorInt, this.denominator);
			if (greatestCommonDivisor != 1) {
				redused = true;
				this.numeratorInt = this.numeratorInt / greatestCommonDivisor;
				this.denominator = this.denominator / greatestCommonDivisor;
			}
		}

		public ISolutionItem getFormula() {
			// Numerator
			ListFormula numeratorFormula = new ListFormula();
			if (this.numeratorInt != 1) {
				numeratorFormula.add(new LetterFormula(Long.toString(this.numeratorInt)));
			}
			numeratorFormula.add(new SqrtFormula(new LetterFormula(Long.toString(this.numeratorSqrt))));

			if (this.denominator == 1) {
				return numeratorFormula;
			}
			return new FractionalFormula(numeratorFormula, new LetterFormula(Long.toString(denominator)));
		}

		public double getFloatValue() {
			return (double) this.numeratorInt * (double) Math.sqrt(this.numeratorSqrt) / (double) this.denominator;
		}

		public boolean isRedused() {
			return redused;
		}

	}

	public Solution getSolution() {
		return solution;
	}

	private void calcVieta() {
		solution.add(new TextPart(context.getString(R.string.solution_vieta_title), true));
		if (x1Double % 1 != 0 || x2Double % 1 != 0) {
			solution.add(new TextPart(context.getString(R.string.solution_vieta_no_1_title)));
			return;
		}

		x1Long = (long) x1Double;
		x2Long = (long) x2Double;

		if (aLong != 1) {
			solution.add(new TextPart(context.getString(R.string.solution_vieta_no_2_title)));
			return;
		}

		solution.add(MathUtils.getVietaTheory());

		long pDouble = bLong;
		long qDouble = cLong;

		solution.add(MathUtils.getEquation(1, pDouble, qDouble));

		equation1 = new ListFormula();
		equation1.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("1")));
		equation1.add(new LetterFormula(".", Alignment.CENTER));
		equation1.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("2")));
		equation1.add(new LetterFormula("="));
		equation1.add(new LetterFormula(MathUtils.getArgDouble(qDouble, false, false)));
		equation1.add(new LetterFormula(";"));

		equation2 = new ListFormula();
		equation2.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("1")));
		equation2.add(new LetterFormula("+"));
		equation2.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("2")));
		equation2.add(new LetterFormula("="));
		equation2.add(new LetterFormula(MathUtils.getArgDouble(-pDouble, false, false)));
		equation2.add(new LetterFormula(";"));

		SystemFormula systemEquation = new SystemFormula(equation1, equation2);
		solution.add(systemEquation);

		XsInvetigation xsInvetigation;
		String investigationText;

		if (qDouble > 0) {
			investigationText = context.getString(R.string.solution_vieta_text_1);
			if (-pDouble > 0) {
				investigationText = investigationText + context.getString(R.string.solution_vieta_text_2);
				xsInvetigation = XsInvetigation.BOTH_POSITIVE;
			} else {
				investigationText = investigationText + context.getString(R.string.solution_vieta_text_3);
				xsInvetigation = XsInvetigation.BOTH_NEGATIVE;
			}
		} else {
			investigationText = context.getString(R.string.solution_vieta_text_4);
			if (-pDouble > 0) {
				investigationText = investigationText + context.getString(R.string.solution_vieta_text_5);
				xsInvetigation = XsInvetigation.LAGER_POSITIVE;
			} else {
				investigationText = investigationText + context.getString(R.string.solution_vieta_text_6);
				xsInvetigation = XsInvetigation.LAGER_NEGATIVE;
			}
		}
		solution.add(new TextPart(investigationText));
		solution.add(new LetterFormula(context.getString(R.string.solution_vieta_text_7)));
		solution.add(equation1);
		StringBuilder multipliers = new StringBuilder(context.getString(R.string.solution_vieta_text_8));
		multipliers.append(qDouble).append(context.getString(R.string.solution_vieta_text_9));
		List<Pair> pairs = getPairs(Math.abs(qDouble), xsInvetigation);
		for (Pair pair : pairs) {
			multipliers.append(" ").append(pair.toString()).append(",");
		}
		multipliers.deleteCharAt(multipliers.length() - 1);
		multipliers.append(".");

		solution.add(new TextPart(multipliers.toString()));
		solution.add(new LetterFormula(context.getString(R.string.solution_vieta_text_10)));
		solution.add(equation2);
		solution.add(new TextPart(context.getString(R.string.solution_vieta_text_11) + x1Long + "; " + x2Long
				+ context.getString(R.string.solution_vieta_text_12)));

		ListFormula result = new ListFormula();
		result.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("1")));
		result.add(new LetterFormula("=" + String.valueOf(x1Long) + "; "));
		result.add(new IndexFormula(new LetterFormula("x"), new LetterFormula("2")));
		result.add(new LetterFormula("=" + String.valueOf(x2Long) + ";"));
		solution.add(result);
	}

	private void calcGraph() {
		// title
		solution.add(new TextPart(context.getString(R.string.solution_graph_title), true));
		// formula
		ListFormula formula = new ListFormula();
		formula.add(new LetterFormula("y="));
		formula.add(MathUtils.getPartOfEquation(aDouble, bDouble, cDouble));
		formula.add(new LetterFormula(";"));
		solution.add(formula);
		solution.add(new Graph(aDouble, bDouble, cDouble, d, x1Double, x2Double));
		// apex
		if (d == 0) {
			solution.add(new LetterFormula("x = " + MathUtils.getPointName(x1Double) + ";"));
			return;
		} else if (d > 0) {
			solution.add(new LetterFormula(context.getString(R.string.solution_graph_text_1)
					+ MathUtils.getPointName(x1Double) + ", " + MathUtils.getPointName(x2Double)));
		}
		double xApex = -bDouble / (2 * aDouble);
		double yApex = aDouble * xApex * xApex + bDouble * xApex + cDouble;
		xApex = MathUtils.round(xApex, 3);
		yApex = MathUtils.round(yApex, 3);
		solution.add(new LetterFormula(context.getString(R.string.solution_graph_text_2) + "("
				+ MathUtils.getPointName(xApex) + ";" + MathUtils.getPointName(yApex) + ")"));
	}

}
