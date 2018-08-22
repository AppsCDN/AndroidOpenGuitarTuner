package com.guitar_tuner_tv.guitartunertv;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sbarjola on 18/08/18.
 */
public class MarkerView extends View {

    private final int DURACION_ANIMACION = 150;
    private Paint dibujado;

    // Marker
    private double markerAngle;
    private int markerColor;

    // Minutes and hours array
    private Map<Float, String> mTickLabels = new HashMap<>();

    //Minutes
    private float minutesMarksLength;
    private int minutesMarksColor;

    // Hours
    private int hoursMarksColors;

    private float mStrokeWidth;
    private float mTextStrokeWidth;
    private float mTickLabelTextSize;
    private float mArcOffset;

    // Text color
    private int colorTexto;

    public MarkerView(Context context) {
        this(context, null);
    }

    public MarkerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Colores
        markerColor = Color.WHITE;
        minutesMarksColor = Color.WHITE;
        hoursMarksColors = Color.WHITE;
        colorTexto = Color.WHITE;

        // Object instances
        dibujado = new Paint();
        init(context);
    }

    private void init(Context context) {
        mTextStrokeWidth = dibujado.getStrokeWidth();
        mStrokeWidth = getResources().getDimension(R.dimen.needle_view_stroke_width);
        mArcOffset = getResources().getDimension(R.dimen.needle_view_ticks_margin_top);
        mTickLabelTextSize = getResources().getDimension(R.dimen.needle_view_tick_label_text_size);
        minutesMarksLength = dpToPixels(context);
        setTipPos(0);
    }

    public static float dpToPixels(Context context) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, r.getDisplayMetrics());
    }

    public void animateTip(float posicionFinal) {

        posicionFinal = Math.min(1.0F, Math.max(-1.0F, posicionFinal));
        final int anchura = getWidth() - getPaddingLeft() - getPaddingRight();
        final int altura = getHeight() - getPaddingTop() - getPaddingBottom();

        double toAngle;

        if(altura > anchura/2f) {
            toAngle = 90 + posicionFinal * (90 - Math.toDegrees(Math.acos(((anchura) / 2.0F - mStrokeWidth) / altura)));
        }else {
            toAngle = 90 + posicionFinal * 90;
        }

        ValueAnimator animator = ValueAnimator.ofFloat((float) markerAngle, (float) toAngle);
        animator.addUpdateListener((ValueAnimator animation) -> {
                markerAngle = (float) animation.getAnimatedValue();
                invalidate();

        });

        animator.setDuration(DURACION_ANIMACION);
        animator.start();
    }

    public void setTipPos(final float position) {

        final int width = getWidth() - getPaddingLeft() - getPaddingRight();
        final int height = getHeight() - getPaddingTop() - getPaddingBottom();

        if(height > width/2f) {
            markerAngle = 90 + position * (90 - Math.toDegrees(Math.acos(((width) / 2.0F - mStrokeWidth) / height)));
        }
        else {
            markerAngle = 90 + position * 90;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        final int width = getWidth() - getPaddingLeft() - getPaddingRight();
        final int height = getHeight() - getPaddingTop() - getPaddingBottom();
        final float tickLabelHeight = dibujado.descent() - dibujado.ascent();

        dibujado.setStyle(Paint.Style.STROKE);
        dibujado.setStrokeCap(Paint.Cap.BUTT);

        drawTickLabels(canvas, width, height);
        drawTicks(canvas, width, height, tickLabelHeight);
        drawMarker(canvas, width, height, tickLabelHeight);
    }

    private void drawTickLabels(Canvas canvas, final int width, final int height) {
        final float cx = width / 2.0F + getPaddingLeft();
        final float cy = height + getPaddingTop();
        dibujado.setColor(colorTexto);
        dibujado.setTextSize(mTickLabelTextSize);
        dibujado.setStrokeWidth(mTextStrokeWidth);

        for (Map.Entry<Float, String> entry : mTickLabels.entrySet()) {
            String text = entry.getValue();
            final float textWidth = dibujado.measureText(text);
            final float pos = entry.getKey();
            if (pos == 0) {
                canvas.drawText(text, (width - textWidth) / 2.0F + getPaddingLeft(), getPaddingTop() - (dibujado.ascent() + 25) / 2.0F, dibujado);
            } else {
                final float angle = (float) (pos * (90 - Math.toDegrees(Math.acos((width / 2.0F) / height))));
                canvas.save();
                canvas.rotate(angle, cx, cy);
                if (pos > 0) {
                    canvas.drawText(text, width / 2.0F - textWidth + getPaddingLeft(), getPaddingTop(), dibujado);
                } else {
                    canvas.drawText(text, width / 2.0F + getPaddingLeft(), getPaddingTop(), dibujado);
                }
                canvas.restore();
            }
        }
    }

    private void drawMarker(Canvas canvas, final int width, final int height, float tickLabelHeight) {

        final double angleRad = Math.toRadians(markerAngle);
        final float needleLength = height - mArcOffset - tickLabelHeight;
        final float cx = width / 2.0F + getPaddingLeft();
        final float cy = height + getPaddingTop();
        final float tipX = (float) (-needleLength * Math.cos(angleRad) + cx);
        final float tipY = (float) (-needleLength * Math.sin(angleRad) + cy);

        // Set the markerColor
        dibujado.setStrokeWidth(mStrokeWidth);
        dibujado.setStrokeCap(Paint.Cap.SQUARE);
        dibujado.setColor(markerColor);

        canvas.drawLine(cx, cy, tipX, tipY, dibujado);
        dibujado.setColor(colorTexto);
        dibujado.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, mStrokeWidth, dibujado);
        dibujado.setStyle(Paint.Style.STROKE);
        dibujado.setStrokeWidth(mStrokeWidth/2f);
        canvas.drawCircle(cx, cy, mStrokeWidth*1.5f, dibujado);
    }

    private void drawTicks(Canvas canvas, final int width, final int height, final float tickLabelHeight) {
        dibujado.setStrokeCap(Paint.Cap.SQUARE);
        final float cx = width / 2.0F + getPaddingLeft();
        final float cy = height + getPaddingTop();
        final float startAngle;

        if(height > width /2f) {
            startAngle = (float) Math.toDegrees(Math.acos((width / 2.0F - mStrokeWidth) / height));
        }else {
            startAngle = 0;
        }

        float currentAngle = startAngle;
        final float endAngle = 180 - startAngle;
        final float midAngle = startAngle + (endAngle - startAngle) / 2.0F;
        final float step = (endAngle - startAngle) / (2.0F * 10);

        drawBigTick(canvas, height, tickLabelHeight, cx, cy, currentAngle);
        currentAngle += step;

        while (currentAngle < midAngle) {
            drawSmallTick(canvas, height, tickLabelHeight, cx, cy, currentAngle);
            currentAngle += step;
        }

        currentAngle = midAngle;
        drawBigTick(canvas, height, tickLabelHeight, cx, cy, currentAngle);
        currentAngle += step;

        while (currentAngle < endAngle) {
            drawSmallTick(canvas, height, tickLabelHeight, cx, cy, currentAngle);
            currentAngle += step;
        }

        currentAngle = endAngle;
        drawBigTick(canvas, height, tickLabelHeight, cx, cy, currentAngle);
    }

    private void drawSmallTick(Canvas canvas, final float height, final float tickLabelHeight, final float cx, final float cy, float angle) {
        dibujado.setColor(minutesMarksColor);
        dibujado.setStrokeWidth(mStrokeWidth / 2.0F);

        final double angleRad = Math.toRadians(angle);
        final float tipX = (float) (-(height - mArcOffset - tickLabelHeight) * Math.cos(angleRad) + cx);
        final float tipY = (float) (-(height - mArcOffset - tickLabelHeight) * Math.sin(angleRad) + cy);

        final float tickLength = minutesMarksLength;

        canvas.drawLine((float) (tipX + Math.cos(angleRad) * tickLength), (float) (tipY + Math.sin(angleRad) * tickLength), tipX, tipY, dibujado);
    }

    private void drawBigTick(Canvas canvas, float height, float tickLabelHeight, float cx, float cy, float angle) {
        dibujado.setColor(hoursMarksColors);
        dibujado.setStrokeWidth(mStrokeWidth);

        final double angleRad = Math.toRadians(angle);
        final float tipX = (float) (-(height - mArcOffset - tickLabelHeight) * Math.cos(angleRad) + cx);
        final float tipY = (float) (-(height - mArcOffset - tickLabelHeight) * Math.sin(angleRad) + cy);

        final float tickLength = minutesMarksLength * 2;

        canvas.drawLine((float) (tipX + Math.cos(angleRad) * tickLength), (float) (tipY + Math.sin(angleRad) * tickLength), tipX, tipY, dibujado);
    }

    // Getters

    public void setTickLabel(final float pos, final String label) {
        mTickLabels.put(pos, label);
    }

}
