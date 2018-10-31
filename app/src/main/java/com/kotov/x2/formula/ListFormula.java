package com.kotov.x2.formula;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kotov.x2.drawer.DrawUtils;
import com.kotov.x2.solution.ISolutionItem;
import com.kotov.x2.solution.ISolutionLine;

public class ListFormula implements ISolutionItem, ISolutionLine {

	private List<ISolutionItem> constructions = new ArrayList<ISolutionItem>();

	public ListFormula() {
	}

	public ListFormula(ISolutionItem arg) {
		add(arg);
	}

	public void add(ISolutionItem arg) {
		constructions.add(arg);
	}

	public void add(List<ISolutionItem> arg) {
		constructions.addAll(arg);
	}
	
	@Override
	public void draw(Canvas canvas, int textSize, int leftX, int bottomY) {
		for (ISolutionItem formula : constructions) {
			Rect bounds = formula.getBounds(textSize);
			formula.draw(canvas, textSize, leftX, bottomY);
			leftX = leftX + bounds.width() + DrawUtils.getDistance(textSize);
		}
	}

	@Override
	public Rect getBounds(int textSize) {
		Rect result = new Rect();
		for (ISolutionItem construction : constructions) {
			Rect constructionBounds = construction.getBounds(textSize);
			if (result.top > constructionBounds.top) {
				result.top = constructionBounds.top;
			}
			if (result.bottom < constructionBounds.bottom) {
				result.bottom = constructionBounds.bottom;
			}
			result.right = result.right + constructionBounds.width() + DrawUtils.getDistance(textSize); 
		}
		result.right = result.right - DrawUtils.getDistance(textSize);
		return result;
	}

	@Override
	public String toString() {
		return "Constructions [contructions=" + constructions + "]";
	}
	
	public int size() {
		return constructions.size();
	}
	
	@Override
	public int getHeightInCells(int textSize) {
		int result = 0;
		for (ISolutionItem formula : constructions) {
			int formulaHeightInCells = formula.getHeightInCells(textSize);
			if (result < formulaHeightInCells) {
				result = formulaHeightInCells;
			}
		}
		return result;
	}
	
}
