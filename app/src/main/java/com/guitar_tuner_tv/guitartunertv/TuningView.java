/*
 * Copyright 2016 andryr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guitar_tuner_tv.guitartunertv;

import android.animation.ValueAnimator;
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

    private int mSelectedIndex;
    private TuningType tuningType;
    private float mTuningItemWidth;
    private Paint mPaint = new Paint();
    private Rect mTempRect = new Rect();
    private int mNormalTextColor;
    private int mSelectedTextColor;
    private float mOffset = 0;
    private ValueAnimator mOffsetAnimator = null;

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
        mNormalTextColor = Color.BLACK;
        mSelectedTextColor = Color.BLACK;
        mPaint.setTextSize(60);
        mTuningItemWidth = Color.BLACK;
    }

    public void setSelectedIndex(int selectedIndex, boolean animate) {
        if (selectedIndex == mSelectedIndex)
            return;

        mSelectedIndex = selectedIndex;
        float newOffset = (getWidth() - mTuningItemWidth) / 2f - mSelectedIndex * mTuningItemWidth;
        stopAnimation();
        if (animate) {
            mOffsetAnimator = ValueAnimator.ofFloat(mOffset, newOffset);
            mOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mOffset = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mOffsetAnimator.start();
        } else {
            mOffset = newOffset;
        }
    }

    public void setSelectedIndex(int selectedIndex) {
        setSelectedIndex(selectedIndex, false);
    }

    private void stopAnimation() {
        if (mOffsetAnimator != null) {
            mOffsetAnimator.cancel();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        stopAnimation();
        mOffset = (w - mTuningItemWidth) / 2f - mSelectedIndex * mTuningItemWidth;
    }

    public void setTuningType(TuningType tuningType) {
        this.tuningType = tuningType;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (tuningType == null) {
            return;
        }

        for (int i = 0; i < tuningType.getNotasAfinacion().length; i++) {
            if (i == mSelectedIndex) {
                mPaint.setColor(mSelectedTextColor);
            } else {
                mPaint.setColor(mNormalTextColor);
            }
            String text = tuningType.getNotasAfinacion()[i].getNombre();
            float textWidth = mPaint.measureText(text);
            mPaint.getTextBounds(text, 0, text.length(), mTempRect);
            canvas.drawText(text, mOffset + i * mTuningItemWidth + (mTuningItemWidth - textWidth) / 2f, (getHeight() + mTempRect.height()) / 2f, mPaint);
        }


    }
}
