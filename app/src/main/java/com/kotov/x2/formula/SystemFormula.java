package com.kotov.x2.formula;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kotov.x2.drawer.DrawUtils;
import com.kotov.x2.solution.ISolutionItem;
import com.kotov.x2.solution.ISolutionLine;

public class SystemFormula implements ISolutionItem, ISolutionLine {
	
	private ISolutionItem arg1;
	private ISolutionItem arg2;
	
	public SystemFormula(ISolutionItem arg1, ISolutionItem arg2) {
		super();
		this.arg1 = arg1;
		this.arg2 = arg2;
	}


	@Override
	public void draw(Canvas canvas, int textSize, int leftX, int bottomY) {
		int cellSize = DrawUtils.getCellSize(textSize);
		arg1.draw(canvas, textSize, leftX + getMarginLeft(textSize), bottomY - cellSize);
		arg2.draw(canvas, textSize, leftX + getMarginLeft(textSize), bottomY + arg1.getHeightInCells(textSize) + DrawUtils.getCellSize(textSize) * 2 - cellSize);
		DrawUtils.drawBrace(canvas, textSize, DrawUtils.getStrokeWidth(textSize), leftX, bottomY + DrawUtils.getCellSize(textSize) * 2 + 1 - cellSize, DrawUtils.getCellSize(textSize) * 3 + 2, getIdentHorizontal(textSize));
	}

	@Override
	public Rect getBounds(int textSize) {
		Rect arg1Bounds = arg1.getBounds(textSize);
		Rect arg2Bounds = arg2.getBounds(textSize);
		int cellSize = DrawUtils.getCellSize(textSize);
		return new Rect(0, arg1Bounds.top - arg1Bounds.bottom - cellSize / 2, Math.max(arg1Bounds.width(), arg2Bounds.width()) + getMarginLeft(textSize), arg2Bounds.bottom - arg2Bounds.top - cellSize + cellSize / 2);
	}
	
	private int getIdentHorizontal(int textSize) {
		return textSize / 10 * 3;
	}
	
	private int getMarginLeft(int textSize) {
		return getIdentHorizontal(textSize) * 4 ;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(arg1.toString());
		result.append("; ");
		result.append(arg2.toString());
		result.append(";");
		return result.toString();
	}
	
	@Override
	public int getHeightInCells(int textSize) {
		return arg1.getHeightInCells(textSize) + arg2.getHeightInCells(textSize) + 1;
	}
	
}
