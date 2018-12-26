package com.kotov.x2.formula;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kotov.x2.drawer.DrawUtils;
import com.kotov.x2.solution.ISolutionItem;
import com.kotov.x2.solution.ISolutionLine;

public class FractionalFormula implements ISolutionItem, ISolutionLine {

	private ISolutionItem numerator;
	private ISolutionItem denominator;

	public FractionalFormula(ISolutionItem numerator, ISolutionItem denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	@Override
	public void draw(Canvas canvas, int textSize, int leftX, int bottomY) {

		Rect numeratorBounds = numerator.getBounds(textSize);
		Rect denominatorBounds = denominator.getBounds(textSize);

		int width = Math.max(numeratorBounds.width(), denominatorBounds.width());
		int sampleHeight = DrawUtils.getSampleHeight(textSize);
		int strokeWidth = DrawUtils.getStrokeWidth(textSize);

		numerator.draw(canvas, textSize, leftX + (width - numeratorBounds.width()) / 2, bottomY - getMarginTop(textSize) - numeratorBounds.bottom - strokeWidth / 2 - sampleHeight / 2);
		DrawUtils.drawLineBySize(canvas, null, strokeWidth, leftX, bottomY - sampleHeight / 2, width, 0);
		
		int denominatorBottomY = bottomY - denominatorBounds.top + getMarginTop(textSize) + strokeWidth - strokeWidth / 2 - sampleHeight + sampleHeight / 2;
		denominator.draw(canvas, textSize, leftX + (width - denominatorBounds.width()) / 2, denominatorBottomY);
	}

	private int getMarginTop(int textSize) {
//		return textSize / 10;
		return 2;
	}

	@Override
	public Rect getBounds(int textSize) {
		Rect numeratorBounds = numerator.getBounds(textSize);
		Rect denominatorBounds = denominator.getBounds(textSize);
		int sampleHeight = DrawUtils.getSampleHeight(textSize);
		int strokeWidth = DrawUtils.getStrokeWidth(textSize);
		return new Rect(0, numeratorBounds.top - numeratorBounds.bottom - getMarginTop(textSize) - strokeWidth / 2 - sampleHeight / 2, Math.max(numeratorBounds.width(),
				denominatorBounds.width()), denominatorBounds.bottom - denominatorBounds.top + getMarginTop(textSize) + strokeWidth - strokeWidth / 2 - sampleHeight + sampleHeight
				/ 2);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("(");
		result.append(numerator.toString());
		result.append(")/(");
		result.append(denominator.toString());
		result.append(")");
		return result.toString();
	}

	@Override
	public int getHeightInCells(int textSize) {
		return numerator.getHeightInCells(textSize) + denominator.getHeightInCells(textSize);
	}

}
