package com.kotov.x2.formula;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;

import com.kotov.x2.calculator.MathUtils;
import com.kotov.x2.drawer.DrawUtils;
import com.kotov.x2.solution.ISolutionLine;

public class Graph implements ISolutionLine {

	private enum PointAxis {
		X, Y;
	}

	private enum XAlign {
		LEFT, CENTER, RIGHT;
	}

	private enum YAlign {
		TOP, CENTER, BOTTOM;
	}

	private class PointTitleAlign {
		private XAlign xAlign;
		private YAlign yAlign;

		public PointTitleAlign(XAlign xAlign, YAlign yAlign) {
			this.xAlign = xAlign;
			this.yAlign = yAlign;
		}

		public Point calcCoords(Point point, Rect bounds, int cellSize) {
			// X
			if (xAlign == XAlign.LEFT) {
				point.x = point.x - bounds.width() - cellSize / 3;
			} else if (xAlign == XAlign.CENTER) {
				point.x = point.x - bounds.width() / 2;
			} else {
				point.x = point.x + cellSize / 3;
			}
			// Y
			if (yAlign == YAlign.CENTER) {
				point.y = point.y + bounds.height() / 2;
			} else if (yAlign == YAlign.BOTTOM) {
				point.y = point.y + bounds.height() + cellSize / 3;
			} else {
				point.y = point.y - cellSize / 3;
			}
			return point;
		}

	}

	private int heightInCells = 0;
	private int sizeInternalCells;
	private Rect bounds;
	private Point sizeInternalPixels;
	private Point zeroInCells;
	private double sizeOfOnePixel;
	private double cellSizeMath;

	private double aDouble;
	private double bDouble;
	private double cDouble;
	private double d;
	private double x1Double;
	private double x2Double;
	private double xApex;
	private double yApex;

	public Graph(double aDouble, double bDouble, double cDouble, double d, double x1Double, double x2Double) {
		this.aDouble = aDouble;
		this.bDouble = bDouble;
		this.cDouble = cDouble;
		this.d = d;
		this.x1Double = x1Double;
		this.x2Double = x2Double;
		if (aDouble != 0) {
			xApex = -bDouble / (2 * aDouble);
			yApex = calcY(xApex);
		}
	}

	@Override
	public void draw(Canvas canvas, int textSize, int leftX, int bottomY) {
		int cellSize = DrawUtils.getCellSize(textSize);
		bottomY = bottomY - cellSize;
		leftX = 0;
		// init
		getBounds(textSize);
		getHeightInCells(textSize);
		// get min and max values of X and Y
		MinMax xMath;
		MinMax yMath;
		if (d > 0) {
			xMath = getMinMaxMath(x1Double * 1.2, x2Double * 1.2);
			yMath = getMinMaxMath(0, yApex * 1.2);
		} else if (d == 0) {
			if (x1Double == 0) {
				xMath = new MinMax(-5, 5);
				if (aDouble > 0) {
					yMath = new MinMax(0, 5);
				} else if (aDouble < 0) {
					yMath = new MinMax(0, -5);
				} else {
					yMath = new MinMax(-5, 5);
				}
			} else {
				xMath = getMinMaxMath(0, x1Double * 1.5);
				if (aDouble > 0) {
					yMath = new MinMax(0, Math.abs(x1Double));
				} else {
					yMath = new MinMax(0, -Math.abs(x1Double));
				}
			}
		} else {
			xMath = getMinMaxMath(0, xApex * 1.5);
			yMath = getMinMaxMath(0, yApex * 1.5);
		}
		// определяем размер поля графика (математическая вличина)
		double areaSizeMath = Math.max(xMath.getSize(), yMath.getSize());
		// определяем размер одной клетоки
		cellSizeMath = areaSizeMath / (sizeInternalCells - 2);
		// округляем размер клетки в меньшую сторону
		cellSizeMath = roundOneCellSize(cellSizeMath);
		// вычисляем новый размер поля графика исходя из нового размера клетки
		areaSizeMath = cellSizeMath * sizeInternalCells;
		// вычисляем новые значения минимума и максимума по оси х
		double size = xMath.getSize();
		xMath.min = xMath.min - (areaSizeMath - size) / 2;
		xMath.max = xMath.max + (areaSizeMath - size) / 2;
		// вычисляем новые значения минимума и максимума по оси у
		size = yMath.getSize();
		if (d <= 0) {
			if (aDouble > 0) {
				yMath.max = yMath.max + (areaSizeMath - size);
			} else {
				yMath.min = yMath.min - (areaSizeMath - size);
			}
		} else {
			if (aDouble > 0) {
				yMath.max = yMath.max + areaSizeMath - size;
			} else {
				yMath.min = yMath.min - areaSizeMath + size;
			}
		}
		zeroInCells = getZeroInCells(xMath.min, yMath.max, cellSizeMath);

		if (zeroInCells.x < 2) {
			zeroInCells.x = 2;
		} else if (sizeInternalCells - zeroInCells.x < 2) {
			zeroInCells.x = sizeInternalCells - 2;
		}
		if (zeroInCells.y < 2) {
			zeroInCells.y = 2;
		} else if (sizeInternalCells - zeroInCells.y < 2) {
			zeroInCells.y = sizeInternalCells - 2;
		}
		xMath.min = -zeroInCells.x * cellSizeMath;
		xMath.max = (sizeInternalCells - zeroInCells.x) * cellSizeMath;
		yMath.min = -(sizeInternalCells - zeroInCells.y) * cellSizeMath;
		yMath.max = zeroInCells.y * cellSizeMath;
		sizeOfOnePixel = areaSizeMath / sizeInternalPixels.x;
		//
		drawAxis(canvas, textSize, leftX + cellSize, bottomY + cellSize);
		drawGraph(canvas, textSize, leftX + cellSize, bottomY + cellSize, xMath, yMath);
		drawApex(canvas, textSize, leftX + cellSize, bottomY + cellSize);
		drawRoots(canvas, textSize, leftX + cellSize, bottomY + cellSize);
	}

	private Point getZeroInCells(double minX, double maxY, double cellSizeMath2) {
		return new Point((int) Math.round(Math.abs(minX / cellSizeMath)), (int) Math.round(Math.abs(maxY / cellSizeMath)));
	}

	private class MinMax {
		double min = 0;
		double max = 0;

		public MinMax() {
		}

		public MinMax(double min, double max) {
			this.min = min;
			this.max = max;
		}

		public double getSize() {
			return max - min;
		}

		public boolean isIn(double point) {
			return (min <= point && point <= max);
		}
	}

	private MinMax getMinMaxMath(double point1, double point2) {
		MinMax result = new MinMax();
		result.min = Math.min(0, Math.min(point1, point2));
		result.max = Math.max(0, Math.max(point1, point2));
		return result;
	}

	private void drawApex(Canvas canvas, int textSize, int leftX, int bottomY) {
		if (d == 0) {
			return;
		}
		int color = Color.rgb(0, 84, 0);
		int cellSize = DrawUtils.getCellSize(textSize);
		int strokeWidth = DrawUtils.getStrokeWidth(textSize);

		int sX1 = convertX(leftX, cellSize, xApex);
		int sY1 = convertY(bottomY, cellSize, yApex);
		// Y
		double x2 = 0;
		double y2 = yApex;
		int sX2 = convertX(leftX, cellSize, x2);
		int sY2 = convertY(bottomY, cellSize, y2);
		float[] intervals = new float[] { cellSize * 2 / 3, cellSize / 3 };
		DrawUtils.drawLineByPoints(canvas, strokeWidth, sX1, sY1, sX2, sY2, color, intervals);
		drawPoint(canvas, strokeWidth, textSize, leftX, bottomY, 0, yApex, PointAxis.Y, color);
		// X
		x2 = xApex;
		y2 = 0;
		sX2 = convertX(leftX, cellSize, x2);
		sY2 = convertY(bottomY, cellSize, y2);
		DrawUtils.drawLineByPoints(canvas, strokeWidth, sX1, sY1, sX2, sY2, color, intervals);
		drawPoint(canvas, strokeWidth, textSize, leftX, bottomY, xApex, 0, PointAxis.X, color);
	}

	private void drawRoots(Canvas canvas, int textSize, int leftX, int bottomY) {
		if (d < 0) {
			return;
		}

		YAlign yAlign = YAlign.TOP;
		;
		if (aDouble > 0) {
			yAlign = YAlign.BOTTOM;
		}

		if (d == 0) {
			drawRoot(canvas, textSize, leftX, bottomY, x1Double, new PointTitleAlign(XAlign.CENTER, yAlign));
		} else {
			drawRoot(canvas, textSize, leftX, bottomY, Math.min(x1Double, x2Double), new PointTitleAlign(XAlign.LEFT,
					yAlign));
			drawRoot(canvas, textSize, leftX, bottomY, Math.max(x1Double, x2Double), new PointTitleAlign(XAlign.RIGHT,
					yAlign));
		}
	}

	private void drawRoot(Canvas canvas, int textSize, int leftX, int bottomY, double x, PointTitleAlign pointTitleAlign) {
		int strokeWidth = DrawUtils.getStrokeWidth(textSize);
		drawPoint(canvas, strokeWidth, textSize, leftX, bottomY, x, 0, null, PointAxis.X, pointTitleAlign);
		// drawPoint(canvas, strokeWidth, textSize, leftX, bottomY, x, 0,
		// MathUtils.getPointName(x), PointAxis.X, pointTitleAlign);
	}

	private double roundOneCellSize(double oneCellSize) {
		int i = -5;
		while (true) {
			double m = Math.pow(10, i);
			int j = 1;
			while (j <= 8) {
				double m2 = m * j;
				if (oneCellSize < m2) {
					return m2;
				}
				j++;
				if (j == 3 || j == 7) {
					j++;
				}
			}
			i++;
		}
	}

	private void drawGraph(Canvas canvas, int textSize, int leftX, int bottomY, MinMax xMath, MinMax yMath) {
		int cellSize = DrawUtils.getCellSize(textSize);
		int strokeWidth = DrawUtils.getStrokeWidth(textSize) + 1;
		double x = xMath.min;
		double step = xMath.getSize() / sizeInternalPixels.x / 3;
		double x1 = x;
		double y1 = calcY(x);
		int sX1 = convertX(leftX, cellSize, x1);
		int sY1 = convertY(bottomY, cellSize, y1);
		x = x + step;
		while (x <= xMath.max) {
			double x2 = x;
			double y2 = calcY(x);
			int sX2 = convertX(leftX, cellSize, x2);
			int sY2 = convertY(bottomY, cellSize, y2);
			if (yMath.isIn(y1) && yMath.isIn(y2)) {
				DrawUtils.drawLineByPoints(canvas, strokeWidth, sX1, sY1, sX2, sY2, Color.BLACK);
			}
			x1 = x2;
			y1 = y2;
			sX1 = sX2;
			sY1 = sY2;
			x = x + step;
		}
	}

	private int convertX(int leftX, int cellSize, double x) {
		return (int) (x / sizeOfOnePixel) + zeroInCells.x * cellSize + leftX;
	}

	private int convertY(int bottomY, int cellSize, double y) {
		return (int) (-y / sizeOfOnePixel) + zeroInCells.y * cellSize + bottomY;
	}

	private void drawAxis(Canvas canvas, int textSize, int leftX, int bottomY) {
		int color = Color.BLUE;
		int cellSize = DrawUtils.getCellSize(textSize);
		int strokeWidth = Math.max(1, DrawUtils.getStrokeWidth(textSize) - 1);
		int indent1 = textSize / 3;
		int indent2 = textSize / 5;
		// point zero
		int x0 = leftX + zeroInCells.x * cellSize;
		int y0 = bottomY + zeroInCells.y * cellSize;
		// up, down
		DrawUtils.drawLineByPoints(canvas, strokeWidth, x0, bottomY, x0, bottomY + sizeInternalPixels.y, color);
		DrawUtils.drawLineByPoints(canvas, strokeWidth, x0, bottomY + 2, x0 - indent2, bottomY + cellSize - 2, color);
		DrawUtils.drawLineByPoints(canvas, strokeWidth, x0, bottomY + 2, x0 + indent2, bottomY + cellSize - 2, color);
		DrawUtils.drawText(canvas, "y", textSize, x0 + indent1, bottomY + cellSize, color);
		// mark cells and value up
		for (int i = 2; i < zeroInCells.y - 1; i = i + 2) {
			double point = cellSizeMath * i;
			drawPoint(canvas, strokeWidth, textSize, leftX, bottomY, 0, point, MathUtils.getPointName(point),
					PointAxis.Y, new PointTitleAlign(XAlign.RIGHT, YAlign.CENTER), color);
		}
		// mark cells and value down
		for (int i = 2; i < sizeInternalCells - zeroInCells.y; i = i + 2) {
			double point = -cellSizeMath * i;
			drawPoint(canvas, strokeWidth, textSize, leftX, bottomY, 0, point, MathUtils.getPointName(point),
					PointAxis.Y, new PointTitleAlign(XAlign.RIGHT, YAlign.CENTER), color);
		}

		// left, right
		DrawUtils.drawLineByPoints(canvas, strokeWidth, leftX, y0, leftX + sizeInternalPixels.x, y0, color);
		DrawUtils.drawLineByPoints(canvas, strokeWidth, leftX + sizeInternalPixels.x - 2, y0, leftX
				+ sizeInternalPixels.x - cellSize + 2, y0 - indent2, color);
		DrawUtils.drawLineByPoints(canvas, strokeWidth, leftX + sizeInternalPixels.x - 2, y0, leftX
				+ sizeInternalPixels.x - cellSize + 2, y0 + indent2, color);
		DrawUtils.drawText(canvas, "x", textSize, leftX + sizeInternalPixels.x - cellSize, y0 - indent1, color);
		// mark 2 cells and value left
		for (int i = 2; i < zeroInCells.x; i = i + 2) {
			double point = -cellSizeMath * i;
			drawPoint(canvas, strokeWidth, textSize, leftX, bottomY, point, 0, MathUtils.getPointName(point),
					PointAxis.X, new PointTitleAlign(XAlign.CENTER, YAlign.BOTTOM), color);
		}
		// mark 2 cells and value right
		for (int i = 2; i < sizeInternalCells - zeroInCells.x - 1; i = i + 2) {
			double point = cellSizeMath * i;
			drawPoint(canvas, strokeWidth, textSize, leftX, bottomY, point, 0, MathUtils.getPointName(point),
					PointAxis.X, new PointTitleAlign(XAlign.CENTER, YAlign.BOTTOM), color);
		}
	}

	private void drawPoint(Canvas canvas, int strokeWidth, int textSize, int leftX, int bottomY, double x, double y,
			String pointName, PointAxis pointAxis, PointTitleAlign pointTitleAlign) {
		drawPoint(canvas, strokeWidth, textSize, leftX, bottomY, x, y, pointName, pointAxis, pointTitleAlign,
				Color.BLACK);
	}

	private void drawPoint(Canvas canvas, int strokeWidth, int textSize, int leftX, int bottomY, double x, double y,
			String pointName, PointAxis pointAxis, PointTitleAlign pointTitleAlign, int color) {
		int cellSize = DrawUtils.getCellSize(textSize);
		int pointSize = cellSize / 7;
		int sX = convertX(leftX, cellSize, x);
		int sY = convertY(bottomY, cellSize, y);
		if (pointAxis == PointAxis.X) {
			DrawUtils.drawLineByPoints(canvas, strokeWidth, sX, sY - pointSize, sX, sY + pointSize, color);
		} else {
			DrawUtils.drawLineByPoints(canvas, strokeWidth, sX - pointSize, sY, sX + pointSize, sY, color);
		}

		if (pointName != null) {
			int titleTextSize = textSize * 2 / 3;
			Point titleCoords = pointTitleAlign.calcCoords(new Point(sX, sY),
					DrawUtils.getTextBounds(canvas, pointName, titleTextSize), cellSize);
			DrawUtils.drawText(canvas, pointName, titleTextSize, titleCoords.x, titleCoords.y, color);
		}
	}

	private void drawPoint(Canvas canvas, int strokeWidth, int textSize, int leftX, int bottomY, double x, double y,
			PointAxis pointAxis, int color) {
		int cellSize = DrawUtils.getCellSize(textSize);
		int pointSize = cellSize / 7;
		int sX = convertX(leftX, cellSize, x);
		int sY = convertY(bottomY, cellSize, y);
		if (pointAxis == PointAxis.X) {
			DrawUtils.drawLineByPoints(canvas, strokeWidth, sX, sY - pointSize, sX, sY + pointSize, color);
		} else {
			DrawUtils.drawLineByPoints(canvas, strokeWidth, sX - pointSize, sY, sX + pointSize, sY, color);
		}
	}

	@Override
	public Rect getBounds(int textSize) {
		if (bounds == null) {
			getHeightInCells(textSize);
			int cellSize = DrawUtils.getCellSize(textSize);
			bounds = new Rect(0, 0, heightInCells * cellSize, heightInCells * cellSize);
			sizeInternalPixels = new Point(sizeInternalCells * cellSize, sizeInternalCells * cellSize);
		}
		return bounds;
	}

	@Override
	public int getHeightInCells(int textSize) {
		if (heightInCells == 0) {
			int cellSize = DrawUtils.getCellSize(textSize);
			heightInCells = ((int) Math.min(DrawUtils.screenHeight, DrawUtils.screenWidth) / cellSize);
			sizeInternalCells = heightInCells - 2;
		}
		return heightInCells;
	}

	private double calcY(double x) {
		return aDouble * x * x + bDouble * x + cDouble;
	}

}
