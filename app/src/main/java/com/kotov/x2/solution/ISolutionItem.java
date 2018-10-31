package com.kotov.x2.solution;

import android.graphics.Canvas;
import android.graphics.Rect;

public interface ISolutionItem {

	abstract void draw(Canvas canvas, int textSize, int leftX, int bottomY);
	abstract Rect getBounds(int textSize);
	abstract int getHeightInCells(int textSize);


}
