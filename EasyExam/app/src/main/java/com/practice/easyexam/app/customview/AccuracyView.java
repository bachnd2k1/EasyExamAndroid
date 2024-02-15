package com.practice.easyexam.app.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.practice.easyexam.R;

public class AccuracyView extends View {

    private int accuracyPercentage;

    public AccuracyView(Context context) {
        super(context);
    }

    public AccuracyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccuracyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAccuracyPercentage(int accuracyPercentage) {
        this.accuracyPercentage = accuracyPercentage;
        invalidate(); // Trigger a redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        // Draw circular chart
        Paint chartPaint = new Paint();
        chartPaint.setAntiAlias(true);
        chartPaint.setStyle(Paint.Style.STROKE);
        chartPaint.setStrokeWidth(30);
        chartPaint.setColor(Color.LTGRAY);

        RectF rectF = new RectF(50, 50, viewWidth - 50, viewHeight - 50);
        canvas.drawArc(rectF, -90, 360, false, chartPaint);

        // Draw filled portion of the circular chart based on accuracy
        Paint filledChartPaint = new Paint();
        filledChartPaint.setAntiAlias(true);
        filledChartPaint.setStyle(Paint.Style.STROKE);
        filledChartPaint.setStrokeWidth(30);
        filledChartPaint.setColor(ContextCompat.getColor(getContext(), R.color.light_green));

        float sweepAngle = (float) (accuracyPercentage * 3.6); // Each percentage is 3.6 degrees
        canvas.drawArc(rectF, -90, sweepAngle, false, filledChartPaint);

        // Draw text
        String accuracyText = accuracyPercentage + "%";
        Paint textPaint = new Paint();
        textPaint.setTextSize(30);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        float textX = viewWidth / 2f;
        float textY = viewHeight / 2f + textPaint.getTextSize() / 3f;
        canvas.drawText(accuracyText, textX, textY, textPaint);
    }
}

