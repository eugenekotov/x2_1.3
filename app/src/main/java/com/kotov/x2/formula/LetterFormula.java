package com.kotov.x2.formula;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kotov.x2.calculator.MathUtils;
import com.kotov.x2.drawer.DrawUtils;
import com.kotov.x2.solution.ISolutionItem;
import com.kotov.x2.solution.ISolutionLine;

public class LetterFormula implements ISolutionItem, ISolutionLine {

	private Alignment alignment = Alignment.BOTTOM;
	private String text;
	private boolean underline = false;

	public enum Alignment {
		CENTER,
		BOTTOM;
	}
	
	public LetterFormula(String text) {
		this(text, false);
	}
	
	public LetterFormula(String text, boolean underline) {
		super();
		this.underline = underline;
		this.text = MathUtils.changeMinusSymbol(text);
	}


	public LetterFormula(String text, Alignment alignment) {
		this.text = text;
		this.alignment = alignment;
	}

	public String getText() {
		return text;
	}

	@Override
	public void draw(Canvas canvas, int textSize, int leftX, int bottomY) {
		int tempBottomY = bottomY;
		Rect bounds = getBounds(textSize);
		if (alignment == Alignment.CENTER) {
			tempBottomY = bottomY - (DrawUtils.getSampleHeight(textSize) - bounds.height()) / 2;
		}
		DrawUtils.drawText(canvas, text, textSize, leftX, tempBottomY);
		if (underline) {
			DrawUtils.drawLineByPoints(canvas, 1, leftX, bottomY + 3, leftX + bounds.width(), bottomY + 3, Color.BLACK);
		}
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public Rect getBounds(int textSize) {
		Rect bounds = new Rect();
		Paint paint = DrawUtils.getPaintText(textSize);
		paint.getTextBounds(text, 0, text.length(), bounds);
		return bounds;
	}

	@Override
	public int getHeightInCells(int textSize) {
		return 1;
	}
	
}
