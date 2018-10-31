package com.kotov.x2.formula;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;

import com.kotov.x2.drawer.DrawUtils;
import com.kotov.x2.solution.ISolutionLine;
import com.kotov.x2.solution.ISolutionPart;

public class TextPart implements ISolutionPart {

	private String text;
	private int lastTextSize = -1;
	private int lastScreenWidth = -1;
	private List<ISolutionLine> solutionLines;
	private boolean underline = false;
	

	public TextPart(String text) {
		this(text, false);
	}
	
	public TextPart(String text, boolean underline) {
		this.text = text;
		this.underline = underline;
	}


	@Override
	public List<ISolutionLine> getSolutionLines(int textSize) {
		if (lastTextSize == textSize && textSize != -1 && lastScreenWidth == DrawUtils.screenWidth && lastScreenWidth != -1 && solutionLines != null) {
			return solutionLines;
		}

		solutionLines = new ArrayList<ISolutionLine>();
		
		lastTextSize = textSize;
		lastScreenWidth = DrawUtils.screenWidth;
		String[] words = text.split("\\s+");
		int cellSize = DrawUtils.getCellSize(textSize);
		int i = 0;
		while (i < words.length) {
			// starting new line
			StringBuilder line = new StringBuilder(words[i]);
			i++;
			while (i < words.length) {
				// can add word to line
				StringBuilder testStr = new StringBuilder(line.toString());
				testStr.append(" ");
				testStr.append(words[i]);
				LetterFormula testFormula = new LetterFormula(testStr.toString());
				Rect bounds = testFormula.getBounds(textSize);
				if (bounds.width() + cellSize * 2 > DrawUtils.screenWidth) {
					// cannot
					break;
				}
				line = testStr;
				i++;
			}
			solutionLines.add(new LetterFormula(line.toString(), underline));
		}
		return solutionLines;
	}

}
