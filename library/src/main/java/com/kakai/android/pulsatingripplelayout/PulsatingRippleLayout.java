package com.kakai.android.pulsatingripplelayout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class PulsatingRippleLayout extends FrameLayout {

    public static final int RIPPLE_FILL = 0;
    public static final int RIPPLE_STROKE = 1;

    private static final int DEFAULT_RIPPLE_COUNT = 6;
    private static final int DEFAULT_DURATION_TIME = 3000;
    private static final float DEFAULT_SCALE = 6.0f;
    private static final int DEFAULT_FILL_TYPE = RIPPLE_FILL;

    @ColorInt
    private int rippleColor;

    private float rippleStrokeWidth;
    private float rippleRadius;
    private int rippleDurationTime;
    private int rippleAmount;
    private int rippleDelay;
    private float rippleScale;
    private int rippleType;
    private Paint paint;
    private boolean animationRunning = false;
    private AnimatorSet animatorSet;
    private ArrayList<Animator> animatorList;
    private LayoutParams rippleParams;
    private ArrayList<RippleView> rippleViewList = new ArrayList<>();

    public PulsatingRippleLayout(Context context) {
        super(context);
    }

    public PulsatingRippleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PulsatingRippleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        if (null == attrs) {
            throw new IllegalArgumentException("Attributes should be provided to this view.");
        }

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PulsatingRippleLayout);
        rippleColor = typedArray.getColor(R.styleable.PulsatingRippleLayout_rb_color, ContextCompat.getColor(context, R.color.rippleColor));
        rippleStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.PulsatingRippleLayout_rb_strokeWidth, getResources().getDimensionPixelSize(R.dimen.rippleStrokeWidth));
        rippleRadius = typedArray.getDimensionPixelSize(R.styleable.PulsatingRippleLayout_rb_radius, getResources().getDimensionPixelSize(R.dimen.rippleRadius));
        rippleDurationTime = typedArray.getInt(R.styleable.PulsatingRippleLayout_rb_duration, DEFAULT_DURATION_TIME);
        rippleAmount = typedArray.getInt(R.styleable.PulsatingRippleLayout_rb_rippleAmount, DEFAULT_RIPPLE_COUNT);
        rippleScale = typedArray.getFloat(R.styleable.PulsatingRippleLayout_rb_scale, DEFAULT_SCALE);
        rippleType = typedArray.getInt(R.styleable.PulsatingRippleLayout_rb_type, DEFAULT_FILL_TYPE);
        typedArray.recycle();

        rippleDelay = rippleDurationTime / rippleAmount;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(rippleColor);

        updateRippleType();

        rippleParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rippleParams.gravity = Gravity.CENTER;

        animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorList = new ArrayList<>();

        for (int i = 0; i < rippleAmount; i++) {
            RippleView rippleView = new RippleView(getContext(), i);
            addView(rippleView, rippleParams);
            rippleViewList.add(rippleView);

            final int idx = i;

            final ValueAnimator sizeAnimator = ValueAnimator.ofFloat(1.0f, rippleScale);
            sizeAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            sizeAnimator.setRepeatMode(ObjectAnimator.RESTART);
            sizeAnimator.setStartDelay(i * rippleDelay);
            sizeAnimator.setDuration(rippleDurationTime);
            sizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int radius = (int) (rippleRadius * (float) animation.getAnimatedValue());
                    rippleViewList.get(idx).setRadius(radius);
                }
            });
            animatorList.add(sizeAnimator);

            final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0f);
            alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
            alphaAnimator.setStartDelay(i * rippleDelay);
            alphaAnimator.setDuration(rippleDurationTime);
            animatorList.add(alphaAnimator);
        }

        animatorSet.playTogether(animatorList);
    }

    private boolean isFill() {
        return rippleType == RIPPLE_FILL;
    }

    private void updateRippleType() {
        if (isFill()) {
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(rippleStrokeWidth);
        }
    }

    /**
     * Sets the ripple color.
     *
     * @param color The color of the ripple
     */
    public void setRippleColor(@ColorInt int color) {
        rippleColor = color;
        paint.setColor(rippleColor);
        invalidate();
    }

    /**
     * Sets the ripple drawing mode.
     * <p>
     * Either RIPPLE_FILL or RIPPLE_STROKE
     *
     * @param type One of the two types described above
     */
    public void setRippleType(int type) {
        rippleType = type;
        updateRippleType();
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRippleAnimation();
    }

    public void startRippleAnimation() {
        if (!isRippleAnimationRunning()) {
            for (RippleView rippleView : rippleViewList) {
                rippleView.setVisibility(VISIBLE);
            }
            animatorSet.start();
            animationRunning = true;
        }
    }

    public void stopRippleAnimation() {
        if (isRippleAnimationRunning()) {
            animatorSet.end();
            animationRunning = false;
        }
    }

    public boolean isRippleAnimationRunning() {
        return animationRunning;
    }

    private class RippleView extends View {

        private int radius;

        public RippleView(Context context, int initialRadius) {
            super(context);
            this.radius = initialRadius;
            this.setVisibility(View.INVISIBLE);
        }

        public void setRadius(int radius) {
            this.radius = radius;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius - (isFill() ? 0 : rippleStrokeWidth), paint);
        }
    }
}