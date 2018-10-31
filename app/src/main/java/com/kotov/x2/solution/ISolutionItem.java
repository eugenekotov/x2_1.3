package com.kotov.x2.solution;

import android.graphics.Canvas;
import android.graphics.Rect;

public interface ISolutionItem {

	public abstract void draw(Canvas canvas, int textSize, int leftX, int bottomY);
	public abstract Rect getBounds(int textSize);
	public abstract int getHeightInCells(int textSize);


}
