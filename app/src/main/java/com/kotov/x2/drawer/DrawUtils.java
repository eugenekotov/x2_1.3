package com.kotov.x2.drawer;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Display;

public final class DrawUtils {

    public static final int interval = 10;
    private static final String SAMPLE_TEXT = "Db8";
    public static int screenWidth;
    public static int screenHeight;

    private static Typeface typeface;

    public static void initScreeSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
    }

    public static Paint getPaintText(int textSize) {
        TextPaint paint = new TextPaint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        paint.setTextAlign(Align.LEFT);
        paint.setTextSize(textSize);
        return paint;
    }

    /*
     * The minus sign is printed off-center (vertical) so break the string and
     * print a minus separately.
     */
    public static Point drawText(Canvas canvas, String text, int textSize, int leftX, int bottomY, int color) {
        Paint paint = getPaintText(textSize);
        paint.setColor(color);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, leftX - bounds.left, bottomY, paint);
        return new Point(leftX + bounds.width() + bounds.left, bottomY);
    }

    public static Point drawText(Canvas canvas, String text, int textSize, int leftX, int bottomY) {
        return drawText(canvas, text, textSize, leftX, bottomY, Color.BLACK);
    }

    public static Rect getTextBounds(Canvas canvas, String text, int textSize) {
        Paint paint = getPaintText(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds;
    }

    public static Point drawSqrt(Canvas canvas, int textSize, int strokeWidth, int leftX, int bottomY, int sizeX, int sizeY, int indentHorizontal, int indentVertical) {

        Paint paint = getDefaultPaint();
        Point point = drawLineBySize(canvas, paint, strokeWidth, leftX, bottomY - sizeY / 2, indentHorizontal, 0);
        point = drawLineBySize(canvas, paint, strokeWidth, point, indentHorizontal, sizeY / 2);
        point = drawLineBySize(canvas, paint, strokeWidth, point, indentHorizontal, -sizeY);
        point = drawLineBySize(canvas, paint, strokeWidth, point, sizeX, 0);
        drawLineBySize(canvas, paint, strokeWidth, point, 0, indentVertical);
        return new Point(leftX + sizeX + indentHorizontal * 3, bottomY);
    }

    public static Point drawBrace(Canvas canvas, int textSize, int strokeWidth, int leftX, int bottomY, int sizeY, int indent) {
        Paint paint = getDefaultPaint();

        Point point = drawLineBySize(canvas, paint, strokeWidth, leftX + indent * 2, bottomY, -indent / 3, 0);
        point = drawLineBySize(canvas, paint, strokeWidth, point, -indent + indent * 2 / 3, -indent + indent / 3);
        point = drawLineBySize(canvas, paint, strokeWidth, point, 0, -sizeY / 2 + indent * 2);

        point = drawLineBySize(canvas, paint, strokeWidth, point, -indent / 3, -indent * 2 / 3);
        point = drawLineBySize(canvas, paint, strokeWidth, point, -indent / 3, -indent + indent * 2 / 3);

        point = drawLineBySize(canvas, paint, strokeWidth, point, -1, 0);
        point = drawLineBySize(canvas, paint, strokeWidth, point, 1, 0);

        point = drawLineBySize(canvas, paint, strokeWidth, point, indent / 3, -indent + indent * 2 / 3);
        point = drawLineBySize(canvas, paint, strokeWidth, point, indent / 3, -indent * 2 / 3);

        point = drawLineBySize(canvas, paint, strokeWidth, point, 0, -sizeY / 2 + indent * 2);

        point = drawLineBySize(canvas, paint, strokeWidth, point, indent - indent * 2 / 3, -indent + indent / 3);
        drawLineBySize(canvas, paint, strokeWidth, point, indent / 3, 0);

        return new Point(leftX + indent * 3, bottomY);
    }

    public static int getSqrtWidth(int textSize, int sizeX) {
        final int indentHorizontal = textSize / 10;
        return sizeX + indentHorizontal * 3;
    }

    private static Paint getDefaultPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        return paint;
    }

    public static Point drawLineBySize(Canvas canvas, Paint paint, int strokeWidth, Point point, int sizeX, int sizeY) {
        return drawLineBySize(canvas, paint, strokeWidth, point.x, point.y, sizeX, sizeY);
    }

    public static Point drawLineBySize(Canvas canvas, Paint paint, int strokeWidth, int x, int y, int sizeX, int sizeY) {
        return drawLineByPoints(canvas, paint, strokeWidth, x, y, x + sizeX, y + sizeY);
    }

    public static Point drawLineByPoints(Canvas canvas, Paint paint, int strokeWidth, int x1, int y1, int x2, int y2) {
        if (paint == null) {
            paint = getDefaultPaint();
        }
        paint.setStrokeWidth(strokeWidth);
        canvas.drawLine(x1, y1, x2, y2, paint);
        return new Point(x2, y2);
    }

    public static Point drawLineByPoints(Canvas canvas, int strokeWidth, int x1, int y1, int x2, int y2, int color) {
        Paint paint = getDefaultPaint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawLine(x1, y1, x2, y2, paint);
        return new Point(x2, y2);
    }

    public static Point drawLineByPoints(Canvas canvas, int strokeWidth, int x1, int y1, int x2, int y2, int color, float[] intervals) {
        Paint paint = getDefaultPaint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setPathEffect(new DashPathEffect(intervals, 0));
        canvas.drawLine(x1, y1, x2, y2, paint);
        return new Point(x2, y2);
    }


    public static int getSampleHeight(int textSize) {
        Rect bounds = new Rect();
        Paint paint = DrawUtils.getPaintText(textSize);
        paint.getTextBounds(SAMPLE_TEXT, 0, SAMPLE_TEXT.length(), bounds);
        return bounds.height();
    }

    public static int getDistance(int textSize) {
        return textSize / 4;
    }

    public static void drawCell(Canvas canvas, int cellSize, int color, Rect area) {
        for (int i = area.left / cellSize; i <= area.right / cellSize; i++) {
            drawLineByPoints(canvas, 1, i * cellSize, area.top, i * cellSize, area.bottom, color);
        }

        for (int i = area.top / cellSize; i <= area.bottom / cellSize; i++) {
            drawLineByPoints(canvas, 1, area.left, i * cellSize, area.right, i * cellSize, color);
        }
    }

    public static void drawCell(Canvas canvas, int textSize, int color) {
        int cellSize = DrawUtils.getCellSize(textSize);
        Rect area = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawCell(canvas, cellSize, color, area);
    }

    public static int getCellSize(int textSize) {
        return DrawUtils.getSampleHeight(textSize) + 2;
    }

    public static int getStrokeWidth(int textSize) {
        return Math.max(1, textSize / 15);
    }

    public static Typeface getTypeface() {
        return typeface;
    }

    public static void setTypeface(Typeface typeface) {
        DrawUtils.typeface = typeface;
    }
}
