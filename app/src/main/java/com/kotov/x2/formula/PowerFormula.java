package com.kotov.x2.formula;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kotov.x2.drawer.DrawUtils;
import com.kotov.x2.solution.ISolutionItem;
import com.kotov.x2.solution.ISolutionLine;

public class PowerFormula implements ISolutionItem, ISolutionLine {

	
	private static final int INTERVAL = 2;
	private ISolutionItem arg;
	private ISolutionItem power;

	public PowerFormula(ISolutionItem arg, ISolutionItem power) {
		this.arg = arg;
		this.power = power;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.kotov.x2.formula.AbstractConstruction#draw(android.graphics.Canvas, int, int, int)
	 * 
	 * Degree printed on height = the highest point of the argument - 1/2 conditional height extent.
	 * highest point of the argument = max (Drawer.getSampleHeight(int), argument.top)
	 * conditional height extent = Drawer.getSampleHeight(int)
	 * 
	 */
	@Override
	public void draw(Canvas canvas, int textSize, int leftX, int bottomY) {
		arg.draw(canvas, textSize, leftX, bottomY);
		Rect argBounds = arg.getBounds(textSize);
		power.draw(canvas, textSize / 2, leftX + argBounds.width() + INTERVAL, getPowerBottomY(bottomY, textSize));
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("(");
		result.append(arg.toString());
		result.append(")^(");
		result.append(power.toString());
		result.append(")");
		return result.toString();
	}

	@Override
	public Rect getBounds(int textSize) {
		// compute the upper bound of its argument
		Rect argBounds = arg.getBounds(textSize);
		int sampleHeight = DrawUtils.getSampleHeight(textSize);
		argBounds.top = Math.min(argBounds.top, - sampleHeight);
		
		// compute with the exponent
		Rect powerBounds = power.getBounds(textSize / 2);
		sampleHeight = DrawUtils.getSampleHeight(textSize / 2);
		argBounds.top = argBounds.top + sampleHeight / 2 + powerBounds.top;
		argBounds.right = argBounds.right  + INTERVAL + powerBounds.width(); 
		return argBounds;
	}
	
	private int getPowerBottomY(int bottomY, int textSize) {
		// compute the upper bound of its argument
		Rect argBounds = arg.getBounds(textSize);
		int sampleHeightArg = DrawUtils.getSampleHeight(textSize);
		int sampleHeightPower = DrawUtils.getSampleHeight(textSize/2);
		return bottomY + Math.min(argBounds.top, - sampleHeightArg) + sampleHeightPower / 2;
	}
	
	@Override
	public int getHeightInCells(int textSize) {
		return arg.getHeightInCells(textSize);
	}
	
}
