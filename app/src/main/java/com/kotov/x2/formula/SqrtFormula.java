package com.kotov.x2.formula;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kotov.x2.drawer.DrawUtils;
import com.kotov.x2.solution.ISolutionItem;
import com.kotov.x2.solution.ISolutionLine;

public class SqrtFormula implements ISolutionItem, ISolutionLine {
	
	private ISolutionItem arg;
	
	public SqrtFormula(ISolutionItem arg) {
		super();
		this.arg = arg;
	}

	@Override
	public void draw(Canvas canvas, int textSize, int leftX, int bottomY) {
		Rect argBounds = arg.getBounds(textSize);
		arg.draw(canvas, textSize, leftX + getMarginLeft(textSize), bottomY);
		DrawUtils.drawSqrt(canvas, textSize, DrawUtils.getStrokeWidth(textSize), leftX, bottomY + argBounds.bottom, argBounds.width() + getMarginRight(textSize), argBounds.height() + getMarginTop(textSize), getIndentHorizontal(textSize), getMarginTop(textSize));
	}

	private int getIndentHorizontal(int textSize) {
		return textSize / 10 + 1;
	}
	
	private int getMarginLeft(int textSize) {
		return getIndentHorizontal(textSize) * 3 + 1;
	}

	private int getMarginRight(int textSize) {
//		return textSize / 20;
		return 2;
	}
	
	private int getMarginTop(int textSize) {
//		return textSize / 10;
		return 2;
	}

	@Override
	public Rect getBounds(int textSize) {
		Rect argBounds = arg.getBounds(textSize);
		argBounds.top = argBounds.top - getMarginTop(textSize) - DrawUtils.getStrokeWidth(textSize);
		argBounds.right = argBounds.right + getMarginLeft(textSize) + getMarginRight(textSize); 
		return argBounds;
	}

	@Override
	public int getHeightInCells(int textSize) {
		return arg.getHeightInCells(textSize);
	}
}
