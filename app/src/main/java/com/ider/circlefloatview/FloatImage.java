package com.ider.circlefloatview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * Created by ider-eric on 2016/10/27.
 */

public class FloatImage extends ImageView {

    private int radius = 100;
    private ValueAnimator animator;
    private int imageWidth, imageHeight;
    private String text;
    private Paint textPaint;
    private Rect textBounds;
    private int textColor;
    private int textSize;

    public FloatImage(Context context) {
        super(context);
        setupAnimator();
    }

    public FloatImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FloatImage);
        radius = array.getDimensionPixelSize(R.styleable.FloatImage_radius, 100);
        imageWidth = array.getDimensionPixelSize(R.styleable.FloatImage_image_width, 0);
        imageHeight = array.getDimensionPixelSize(R.styleable.FloatImage_image_height, 0);

        int textId = array.getResourceId(R.styleable.FloatImage_text, 0);
        if(textId != 0) {
            text = getResources().getString(textId);
            textColor = array.getColor(R.styleable.FloatImage_textColor, 0x000000);
            textSize = array.getDimensionPixelSize(R.styleable.FloatImage_textSize, 40);
            setupTextPaint();
        }
        setupAnimator();

    }

    private void setupTextPaint() {
        textPaint = new Paint();
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        Log.i("tag", "size=" + textSize);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
    }

    private void setupAnimator() {
        // 0~360代表角度
        animator = ValueAnimator.ofFloat(0, 360);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animatorUpdateListener);
        animator.setRepeatCount(ValueAnimator.INFINITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(imageWidth + 2*radius, imageHeight + 2*radius + textBounds.height());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(textPaint == null) {
            return;
        }

        int width = textBounds.width();
        int height = textBounds.height();

        int x = (getWidth() - width) / 2;
        int y = getHeight() - 5;

        canvas.drawText(text, 0, text.length(), x, y, textPaint);

    }

    ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float value = (float) valueAnimator.getAnimatedValue();

            float left = radius * (1 + (float) Math.sin(Math.toRadians(value)));
            float top = radius * (1 - (float) Math.cos(Math.toRadians(value)));
            float right = getWidth() - imageWidth - left;
            float bottom = getHeight() - imageHeight - top;
            setPadding((int) left, (int) top, (int) right, (int) bottom);
            invalidate();
        }
    };


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startFloat();
    }

    public void startFloat() {
        animator.start();
    }
}
