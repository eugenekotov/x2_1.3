package com.kotov.x2.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.kotov.x2.solution.ISolutionItem;

public class FormulaView extends View {
	
	public enum FormulaViewAlignment {
		TOP,
		CENTER,
		BOTTOM;
	}

	private ISolutionItem formula;
	private int textSize = 20;
	private int minimumWidth = 0;
	private int minimumHeight = 0;
	private FormulaViewAlignment alignment = FormulaViewAlignment.TOP;

	public FormulaView(Context context) {
		super(context);
		if (!isInEditMode()) {
			init();
		}
	}

	public FormulaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			init();
		}
	}

	public FormulaView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		if (!isInEditMode()) {
			init();
		}
	}

	private void init() {
	}

	public ISolutionItem getFormula() {
		return formula;
	}

	public void setFormula(ISolutionItem formula) {
		this.formula = formula;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (formula != null) {
			formula.draw(canvas, textSize, 0, getBottomY());
		}
	}
	
	private int getBottomY() {
		Rect bounds = formula.getBounds(textSize);
		if (alignment == FormulaViewAlignment.TOP) {
			return -bounds.top;
		} else if (alignment == FormulaViewAlignment.CENTER) {
			return (getHeight() + bounds.height())/2;
		} else {
			return getHeight();
		}
	}

	public int getTextSize() {
		return textSize;
	}
	
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (formula != null) {
			Rect bounds = formula.getBounds(textSize);
			setMeasuredDimension(Math.max(bounds.width(), minimumWidth) , Math.max(bounds.height(), minimumHeight));
		}
	}

	public int getMinimumWidth() {
		return minimumWidth;
	}

	public void setMinimumWidth(int minimumWidth) {
		super.setMinimumWidth(minimumWidth);
		this.minimumWidth = minimumWidth;
	}

	public int getMinimumHeight() {
		return minimumHeight;
	}

	public void setMinimumHeight(int minimumHeight) {
		super.setMinimumHeight(minimumHeight);
		this.minimumHeight = minimumHeight;
	}

	public FormulaViewAlignment getFormulaViewAlignment() {
		return alignment;
	}

	public void setFormulaViewAlignment(FormulaViewAlignment alignment) {
		this.alignment = alignment;
	}
	
}
