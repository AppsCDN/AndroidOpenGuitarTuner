package com.guitar_tuner_tv.guitartunertv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Provisional class, UI it's going to be remade.
 * Created by sbarjola on 18/08/18.
 */
public class TuningView extends View {

    private TuningType tuningType;
    private float mTuningItemWidth;
    private Paint mPaint = new Paint();
    private Rect mTempRect = new Rect();
    private int mSelectedTextColor;
    private float mOffset = 0;

    public TuningView(Context context) {
        this(context, null);
    }

    public TuningView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TuningView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSelectedTextColor = Color.WHITE;
        mTuningItemWidth = Color.BLACK;
        mPaint.setTextSize(60);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mOffset = (w - mTuningItemWidth) / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (tuningType == null) {
            return;
        }

        String text = "";



        final float textWidth = mPaint.measureText(text);
        mPaint.getTextBounds(text, 0, text.length(), mTempRect);
        canvas.drawText(text, mOffset + mTuningItemWidth + (mTuningItemWidth - textWidth) / 2f, (getHeight() + mTempRect.height()) / 2f, mPaint);
    }

    public void setTuningType(TuningType tuningType) {
        this.tuningType = tuningType;
    }

}
