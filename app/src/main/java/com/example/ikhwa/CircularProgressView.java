package com.example.ikhwa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CircularProgressView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private RectF rectF;
    private float progress = 0; // 0 to 100

    private int progressColor = 0xFF3F51B5;

    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xFFE0E0E0); // Light gray
        backgroundPaint.setStrokeWidth(10);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setColor(progressColor); // use default
        progressPaint.setStrokeWidth(10);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setAntiAlias(true);

        rectF = new RectF();
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
        progressPaint.setColor(progressColor);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float padding = 40;
        float width = getWidth() - padding * 2;
        float height = getHeight() - padding * 2;
        rectF.set(padding, padding, padding + width, padding + height);

        // Draw background circle
        canvas.drawArc(rectF, 0, 360, false, backgroundPaint);

        // Draw progress arc
        canvas.drawArc(rectF, -90, (progress / 100f) * 360f, false, progressPaint);
    }
}
