package com.kotov.x2.solution;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;

import com.kotov.x2.drawer.DrawUtils;

public class Solution {

	private List<ISolutionPart> parts;

	public Solution() {
		parts = new ArrayList<ISolutionPart>();
	}

	public Solution(List<ISolutionPart> parts) {
		this.parts = parts;
	}

	private List<ISolutionLine> getLines(int textSize) {
		List<ISolutionLine> result = new ArrayList<ISolutionLine>();
		for (ISolutionPart formula : parts) {
			result.addAll(formula.getSolutionLines(textSize));
		}
		return result;
	}

	/*
	 * Returns height of solution
	 */
	public int getHeight(int textSize) {
		List<ISolutionLine> lines = getLines(textSize);
		int resultInCells = 0;
		for (ISolutionLine line : lines) {
			resultInCells = resultInCells + line.getHeightInCells(textSize) + 1;
		}
		return (resultInCells + 2) * DrawUtils.getCellSize(textSize);
	}

	public void print(Canvas canvas, int textSize) {
		int cellSize = DrawUtils.getCellSize(textSize);
		int leftX = cellSize;
		int bottomY = cellSize * 3 - 1;
		List<ISolutionLine> lines = getLines(textSize);
		for (ISolutionLine line : lines) {
			int height = line.getHeightInCells(textSize);
			if (height == 2) { 
				line.draw(canvas, textSize, leftX, bottomY + cellSize / 2);
			} else if (height == 3) {
				line.draw(canvas, textSize, leftX, bottomY + cellSize);
			} else {
				line.draw(canvas, textSize, leftX, bottomY);
			}
			bottomY = bottomY + (height + 1) * cellSize;
		}
	}

	public void add(ISolutionPart part) {
		parts.add(part);
	}

	public void add(ISolutionItem item) {
		parts.add(new SolutionPart(item));
	}
	
}
