package com.kotov.x2.solution;

import android.graphics.Canvas;
import android.graphics.Rect;

public interface ISolutionItem {

	void draw(Canvas canvas, int textSize, int leftX, int bottomY);
	Rect getBounds(int textSize);
	int getHeightInCells(int textSize);


}
