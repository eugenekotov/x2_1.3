package com.kotov.x2.formula;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kotov.x2.drawer.DrawUtils;
import com.kotov.x2.solution.ISolutionItem;
import com.kotov.x2.solution.ISolutionLine;

public class IndexFormula implements ISolutionItem, ISolutionLine {
	
	private ISolutionItem arg;
	private ISolutionItem index;

	public IndexFormula(ISolutionItem arg,
			ISolutionItem index) {
		this.arg = arg;
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * @see com.kotov.x2.formula.AbstractConstruction#draw(android.graphics.Canvas, int, int, int)
	 * 
	 * It calculated that the index is not longer sample text
	 * 
	 */
	@Override
	public void draw(Canvas canvas, int textSize, int leftX, int bottomY) {
		arg.draw(canvas, textSize, leftX, bottomY);
		Rect argBounds = arg.getBounds(textSize);
		index.draw(canvas, textSize / 2, leftX + argBounds.width(), getIndexBottomY(bottomY, textSize));
	}
	
	private int getIndexBottomY(int bottomY, int textSize) {
		Rect indexBounds = index.getBounds(textSize / 2);
		int sampleHeight = DrawUtils.getSampleHeight(textSize / 2);
		if (- indexBounds.top <= sampleHeight) {
			return bottomY + sampleHeight / 2;
		} else {
			return bottomY - sampleHeight / 2 - indexBounds.top;
		}
	}
	
	@Override
	public Rect getBounds(int textSize) {
		
		Rect argBounds = arg.getBounds(textSize);
		
		Rect indexBounds = index.getBounds(textSize / 2);

		int sampleHeight = DrawUtils.getSampleHeight(textSize / 2);
		if (indexBounds.height() <= sampleHeight) {
			argBounds.bottom = Math.max(argBounds.bottom, sampleHeight / 2);
		} else {
			argBounds.bottom = Math.max(argBounds.bottom, indexBounds.height() - sampleHeight / 2);
		}
		argBounds.right = argBounds.right + indexBounds.width(); 
		return argBounds;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("(");
		result.append(arg.toString());
		result.append(")index(");
		result.append(index.toString());
		result.append(")");
		return result.toString();
	}
	
	@Override
	public int getHeightInCells(int textSize) {
		return arg.getHeightInCells(textSize);
	}

}
