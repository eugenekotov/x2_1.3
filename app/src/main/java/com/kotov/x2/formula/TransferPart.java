package com.kotov.x2.formula;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;

import com.kotov.x2.drawer.DrawUtils;
import com.kotov.x2.solution.ISolutionItem;
import com.kotov.x2.solution.ISolutionLine;
import com.kotov.x2.solution.ISolutionPart;

public class TransferPart implements ISolutionPart {

	List<ISolutionItem> solutionItems;
	private int lastTextSize = -1;
	private int lastScreenWidth = -1;
	private List<ISolutionLine> solutionLines;

	public TransferPart(List<ISolutionItem> solutionItems) {
		this.solutionItems = solutionItems;
	}

	private boolean isNextTransfer(List<ISolutionItem> formulas, int index) {
		if (index < formulas.size() - 1 && formulas.get(index + 1) instanceof LetterFormula) {
			LetterFormula nextFormula = (LetterFormula) formulas.get(index + 1);
			if (nextFormula.getText().equals("\u2248") || nextFormula.getText().equals("=")) {
				return true;
			}
		}
		return false;
	}

	private String getTransferSymbol(List<ISolutionItem> formulas, int index) {
		String result = "=";
		if (index < formulas.size() - 1 && formulas.get(index + 1) instanceof LetterFormula) {
			LetterFormula nextFormula = (LetterFormula) formulas.get(index + 1);
			if (nextFormula.getText().equals("\u2248")) {
				result = "\u2248";
			}
		}
		return result;
	}

	@Override
	public List<ISolutionLine> getSolutionLines(int textSize) {
		if (lastTextSize == textSize && textSize != -1 && lastScreenWidth == DrawUtils.screenWidth && lastScreenWidth != -1 && solutionLines != null) {
			return solutionLines;
		}

		solutionLines = new ArrayList<ISolutionLine>();
		lastTextSize = textSize;
		lastScreenWidth = DrawUtils.screenWidth;
		String transferSymbol = "=";
		int cellSize = DrawUtils.getCellSize(textSize);

		int posInLine = 0; // position on the line
		int leftX = cellSize;
		ListFormula line = new ListFormula();

		int i = 0;
		while (i < solutionItems.size()) {
			Rect bounds = solutionItems.get(i).getBounds(textSize);
			if (i == 0) {
				// if it is the first formula
				line.add(solutionItems.get(i));
				leftX = leftX + bounds.width() + DrawUtils.getDistance(textSize);
			} else {
				// if it is not first formula
				if (posInLine == 0) {
					// start new line
					solutionLines.add(line);
					line = new ListFormula();
					// if start line, print =
					LetterFormula transferSymbolFormula = new LetterFormula(transferSymbol);
					line.add(transferSymbolFormula);
					Rect transferSymbolBounds = transferSymbolFormula.getBounds(textSize);
					leftX = leftX + transferSymbolBounds.width() + DrawUtils.getDistance(textSize);
					line.add(solutionItems.get(i));
					leftX = leftX + bounds.width() + DrawUtils.getDistance(textSize);
				} else {
					// generate next symbol hyphen
					LetterFormula transferSymbolFormula = new LetterFormula(getTransferSymbol(solutionItems, i));
					Rect transferSymbolBounds = transferSymbolFormula.getBounds(textSize);
					// check line length
					int width = bounds.width();
					if (i < solutionItems.size() - 1) {
						width = width + transferSymbolBounds.width() + DrawUtils.getDistance(textSize);
					}
					if (leftX + width + cellSize >= DrawUtils.screenWidth) {
						// taking on a new line
						leftX = cellSize;
						posInLine = 0;
						continue;
					} else {
						// print formula
						line.add(solutionItems.get(i));
						leftX = leftX + bounds.width() + DrawUtils.getDistance(textSize);
					}
				}
			}
			// print =, if not end formula
			if (i < solutionItems.size() - 1) {
				transferSymbol = getTransferSymbol(solutionItems, i);
				LetterFormula transferSymbolFormula = new LetterFormula(transferSymbol);
				line.add(transferSymbolFormula);
				Rect transferSymbolBounds = transferSymbolFormula.getBounds(textSize);
				leftX = leftX + transferSymbolBounds.width() + DrawUtils.getDistance(textSize);
			}
			if (isNextTransfer(solutionItems, i)) {
				i++;
			}
			posInLine++;
			i++;
		}
		if (line.size() > 0) {
			solutionLines.add(line);
		}
		return solutionLines;
	}

}
