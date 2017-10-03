package cn.shield.view.rangebar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class DistanceRangeBar extends View {
    private static final float DEFAULT_TICK_HEIGHT_DP = 5;
    private static final float DEFAULT_PIN_PADDING_DP = 16;
    private static final float DEFAULT_BAR_WEIGHT_PX = 20;
    private static final float DEFAULT_CIRCLE_BOUNDARY_SIZE = 0;
    private static final int DEFAULT_BAR_COLOR = Color.LTGRAY;
    private static final int DEFAULT_TICK_COLOR = Color.WHITE;
    private static final int DEFAULT_CONNECTING_LINE_COLOR = Color.WHITE;
    private static final float DEFAULT_EXPANDED_PIN_RADIUS_DP = 12;
    private static final float DEFAULT_CIRCLE_SIZE_DP = 2;
    private static final float DEFAULT_BAR_PADDING_BOTTOM_DP = 40;
    private float mTickHeightDP = DEFAULT_TICK_HEIGHT_DP;
    private float mBarWeight = DEFAULT_BAR_WEIGHT_PX;
    private int mBarColor = DEFAULT_BAR_COLOR;
    private float mThumbRadiusDP = DEFAULT_EXPANDED_PIN_RADIUS_DP;
    private int mTickColor = DEFAULT_TICK_COLOR;
    private float mExpandedPinRadius = DEFAULT_EXPANDED_PIN_RADIUS_DP;
    private int mCircleColor = DEFAULT_CONNECTING_LINE_COLOR;
    private int mCircleBoundaryColor = DEFAULT_CONNECTING_LINE_COLOR;
    private float mCircleBoundarySize = DEFAULT_CIRCLE_BOUNDARY_SIZE;
    private float mCircleSize = DEFAULT_CIRCLE_SIZE_DP;
    private int mTickCount;
    private CircleView mScrollPin;
    private Bar mBar;
    private OnRangeBarChangeListener mListener;
    private int mRightIndex;
    private float mPinPadding = DEFAULT_PIN_PADDING_DP;
    private float mBarPaddingBottom = DEFAULT_BAR_PADDING_BOTTOM_DP;
    private int mDiffX;
    private int mDiffY;
    private float mLastX;
    private float mLastY;
    private ArrayList<String> mList = new ArrayList<>();
    private float mMargin = 20;
    private int mTickBoundaryColor;
    private float mTickBoundaryWidth;
    private int mEffectColor, mChooseTextColor, mUnChooseTextColor;
    private float mBarTextSize;
    private float mBarTextMarginRangeBar;
    private Context mContext;

    public DistanceRangeBar(Context context) {
        this(context, null);
    }

    public DistanceRangeBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DistanceRangeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        rangeBarInit(context, attrs);
    }

    private void rangeBarInit(Context context, AttributeSet attrs) {
        mContext = context;
        if (attrs == null) throw new NullPointerException("attrs can not be null!");
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DistanceRangeBar, 0, 0);
        try {
            mBarTextMarginRangeBar = ta.getDimension(R.styleable.DistanceRangeBar_barTextMarginRangeBar, TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));
            mBarTextSize = ta.getDimension(R.styleable.DistanceRangeBar_barTextSize, TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
            mChooseTextColor = ta.getColor(R.styleable.DistanceRangeBar_chooseTextColor, 0xff16c5ad);
            mUnChooseTextColor = ta.getColor(R.styleable.DistanceRangeBar_unChooseTextColor, 0xffbbbbbb);
            mEffectColor = ta.getColor(R.styleable.DistanceRangeBar_effectColor, 0xffeeeeee);
            mMargin = ta.getDimension(R.styleable.DistanceRangeBar_tickMargin, 20);
            mTickBoundaryColor = ta.getColor(R.styleable.DistanceRangeBar_tickBoundaryColor, 0xff499bd7);
            mTickBoundaryWidth = ta.getDimension(R.styleable.DistanceRangeBar_tickBoundaryWidth, DEFAULT_CIRCLE_BOUNDARY_SIZE);
            mTickHeightDP = ta
                    .getDimension(R.styleable.DistanceRangeBar_tickHeight, DEFAULT_TICK_HEIGHT_DP);
            mBarWeight = ta.getDimension(R.styleable.DistanceRangeBar_barWeight, DEFAULT_BAR_WEIGHT_PX);
            mBarColor = ta.getColor(R.styleable.DistanceRangeBar_rangeBarColor, DEFAULT_BAR_COLOR);
            mCircleSize = ta.getDimension(R.styleable.DistanceRangeBar_selectorSize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_SIZE_DP,
                            getResources().getDisplayMetrics())
            );

            mCircleColor = ta.getColor(R.styleable.DistanceRangeBar_selectorColor,
                    DEFAULT_CONNECTING_LINE_COLOR);
            mCircleBoundaryColor = ta.getColor(R.styleable.DistanceRangeBar_selectorBoundaryColor,
                    DEFAULT_CONNECTING_LINE_COLOR);
            mCircleBoundarySize = ta.getDimension(R.styleable.DistanceRangeBar_selectorBoundarySize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_BOUNDARY_SIZE,
                            getResources().getDisplayMetrics())
            );

            mTickColor = ta.getColor(R.styleable.DistanceRangeBar_tickColor, DEFAULT_TICK_COLOR);
            mExpandedPinRadius = ta
                    .getDimension(R.styleable.DistanceRangeBar_pinRadius, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            DEFAULT_EXPANDED_PIN_RADIUS_DP, getResources().getDisplayMetrics()));
            mPinPadding = ta.getDimension(R.styleable.DistanceRangeBar_pinPadding, TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PIN_PADDING_DP,
                            getResources().getDisplayMetrics()));
            mBarPaddingBottom = ta.getDimension(R.styleable.DistanceRangeBar_rangeBarPaddingBottom,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            DEFAULT_BAR_PADDING_BOTTOM_DP, getResources().getDisplayMetrics()));
        } finally {
            ta.recycle();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("TICK_COUNT", mTickCount);
        bundle.putInt("TICK_COLOR", mTickColor);
        bundle.putFloat("TICK_HEIGHT_DP", mTickHeightDP);
        bundle.putFloat("BAR_WEIGHT", mBarWeight);
        bundle.putInt("BAR_COLOR", mBarColor);
        bundle.putFloat("CIRCLE_SIZE", mCircleSize);
        bundle.putInt("CIRCLE_COLOR", mCircleColor);
        bundle.putInt("CIRCLE_BOUNDARY_COLOR", mCircleBoundaryColor);
        bundle.putFloat("CIRCLE_BOUNDARY_WIDTH", mCircleBoundarySize);
        bundle.putFloat("THUMB_RADIUS_DP", mThumbRadiusDP);
        bundle.putFloat("EXPANDED_PIN_RADIUS_DP", mExpandedPinRadius);
        bundle.putFloat("PIN_PADDING", mPinPadding);
        bundle.putFloat("BAR_PADDING_BOTTOM", mBarPaddingBottom);
        bundle.putInt("RIGHT_INDEX", mRightIndex);
        bundle.putFloat("MARGIN", mMargin);
        bundle.putInt("TICK_BOUNDARY_COLOR", mTickBoundaryColor);
        bundle.putFloat("TICK_BOUNDARY_WIDTH", mTickBoundaryWidth);
        bundle.putInt("EFFECT_COLOR", mEffectColor);
        bundle.putInt("CHOOSE_TEXT_COLOR", mChooseTextColor);
        bundle.putInt("UNCHOOSE_TEXT_COLOR", mUnChooseTextColor);
        bundle.putFloat("BAR_TEXT_SIZE", mBarTextSize);
        bundle.putFloat("BAR_TEXT_MARGIN_RANGE_BAR", mBarTextMarginRangeBar);
        bundle.putStringArrayList("LIST", mList);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mTickCount = bundle.getInt("TICK_COUNT");
            mTickColor = bundle.getInt("TICK_COLOR");
            mTickHeightDP = bundle.getFloat("TICK_HEIGHT_DP");
            mBarWeight = bundle.getFloat("BAR_WEIGHT");
            mBarColor = bundle.getInt("BAR_COLOR");
            mCircleSize = bundle.getFloat("CIRCLE_SIZE");
            mCircleColor = bundle.getInt("CIRCLE_COLOR");
            mCircleBoundaryColor = bundle.getInt("CIRCLE_BOUNDARY_COLOR");
            mCircleBoundarySize = bundle.getFloat("CIRCLE_BOUNDARY_WIDTH");
            mThumbRadiusDP = bundle.getFloat("THUMB_RADIUS_DP");
            mExpandedPinRadius = bundle.getFloat("EXPANDED_PIN_RADIUS_DP");
            mPinPadding = bundle.getFloat("PIN_PADDING");
            mBarPaddingBottom = bundle.getFloat("BAR_PADDING_BOTTOM");
            mRightIndex = bundle.getInt("RIGHT_INDEX");
            mMargin = bundle.getFloat("MARGIN");
            mTickBoundaryColor = bundle.getInt("TICK_BOUNDARY_COLOR");
            mTickBoundaryWidth = bundle.getFloat("TICK_BOUNDARY_WIDTH");
            mEffectColor = bundle.getInt("EFFECT_COLOR");
            mChooseTextColor = bundle.getInt("CHOOSE_TEXT_COLOR");
            mUnChooseTextColor = bundle.getInt("UNCHOOSE_TEXT_COLOR");
            mBarTextSize = bundle.getFloat("BAR_TEXT_SIZE");
            mBarTextMarginRangeBar = bundle.getFloat("BAR_TEXT_MARGIN_RANGE_BAR");
            mList = bundle.getStringArrayList("LIST");
            setRangePinsByIndices(mRightIndex);
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width;
        int height;

        final int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (measureWidthMode == MeasureSpec.AT_MOST) {
            width = measureWidth;
        } else if (measureWidthMode == MeasureSpec.EXACTLY) {
            width = measureWidth;
        } else {
            width = 500;
        }

        int defaultHeight = 150;
        if (measureHeightMode == MeasureSpec.AT_MOST) {
            height = Math.min(defaultHeight, measureHeight);
        } else if (measureHeightMode == MeasureSpec.EXACTLY) {
            height = measureHeight;
        } else {
            height = defaultHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        final Context ctx = getContext();

        final float yPos = h - mBarPaddingBottom;
        mScrollPin = new CircleView(ctx, yPos);

        final float marginLeft = Math.max(mExpandedPinRadius, mCircleSize);

        final float barLength = w - (2 * marginLeft);
        mBar = new Bar(marginLeft, yPos, barLength);

        mScrollPin.setX(marginLeft + mMargin + (mRightIndex / (float) (mTickCount)) * barLength);

        final int newRightIndex = mBar.getNearestTickIndex(mScrollPin);

        if (newRightIndex != mRightIndex && mListener != null)
            mListener.onRangeChangeListener(this, mRightIndex, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBar.draw(canvas);
        mBar.drawTicks(canvas);
        mScrollPin.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDiffX = 0;
                mDiffY = 0;
                mLastX = event.getX();
                mLastY = event.getY();
                onActionDown(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                onActionUp(event.getX());
                return true;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event.getX());
                this.getParent().requestDisallowInterceptTouchEvent(true);
                final float curX = event.getX();
                final float curY = event.getY();
                mDiffX += Math.abs(curX - mLastX);
                mDiffY += Math.abs(curY - mLastY);
                mLastX = curX;
                mLastY = curY;

                if (mDiffX < mDiffY) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
                return true;

            default:
                return false;
        }
    }

    public void setOnRangeBarChangeListener(OnRangeBarChangeListener listener) {
        mListener = listener;
    }

    public void setTickNum(int tickNum) {
        setTickNum_(tickNum);
    }

    public void setTickNum(ArrayList<String> list) {
        if (list == null || list.size() == 0) return;
        mList.clear();
        mList.addAll(list);
        int tickCount = list.size();
        setTickNum_(tickCount);
    }

    private void setTickNum_(int tickCount) {
        if (isValidTickCount(tickCount)) {
            mTickCount = tickCount;
            if (indexOutOfRange(mRightIndex)) {
                mRightIndex = mTickCount;
                if (mListener != null) mListener.onRangeChangeListener(this, mRightIndex, false);
            }
            createBar();
            createPins();
        } else {
            throw new IllegalArgumentException("tickCount less than 2; invalid tickCount.");
        }
    }

    public int getTickCount() {
        return mTickCount;
    }

    public void setRangePinsByIndices(int rightPinIndex) {
        if (!indexOutOfRange(rightPinIndex)) {
            mRightIndex = rightPinIndex;
            createPinsWhenChangeIndex();
            if (mListener != null) mListener.onRangeChangeListener(this, mRightIndex, false);
            invalidate();
            requestLayout();
        }
    }

    private void createBar() {
        mBar = new Bar(getMarginLeft(), getYPos(), getBarLength());
        invalidate();
    }

    private void createPins() {
        mScrollPin = new CircleView(getContext(), getYPos());
        mScrollPin.setX(getMarginLeft() + (mRightIndex / (float) (mTickCount)) * getBarLength());
        invalidate();
    }

    private void createPinsWhenChangeIndex() {
        mScrollPin = new CircleView(getContext(), getYPos());
        mScrollPin.setX(mBar.getXCoordinateList().size() == 0 ? 0 : mBar.getXCoordinateList().get(mRightIndex));
    }

    private float getMarginLeft() {
        return Math.max(mExpandedPinRadius, mCircleSize);
    }

    private float getYPos() {
        return (getHeight() - mBarPaddingBottom);
    }

    private float getBarLength() {
        return (getWidth() - 2 * getMarginLeft());
    }

    private boolean indexOutOfRange(int rightThumbIndex) {
        return (0 >= mTickCount || rightThumbIndex < 0 || rightThumbIndex >= mTickCount);
    }

    private boolean isValidTickCount(int tickCount) {
        return (tickCount > 1);
    }

    private void onActionDown(float x, float y) {
        if (mScrollPin.isInTargetZone(x, y)) pressPin(mScrollPin);
    }

    private void onActionUp(float x) {
        if (mScrollPin.isPressed()) {
            releasePin(mScrollPin);
            //回调处理 滑动后手抬起的状态
            if (mListener != null) mListener.onRangeChangeListener(this, mRightIndex, false);
        } else {
            mScrollPin.setX(x);
            releasePin(mScrollPin);
            final int newRightIndex = mBar.getNearestTickIndex(mScrollPin);
            if (newRightIndex != mRightIndex) {
                mRightIndex = newRightIndex;
                if (mListener != null) mListener.onRangeChangeListener(this, mRightIndex, false);
            }
        }
    }

    private void onActionMove(float x) {
        if (mScrollPin.isPressed()) movePin(mScrollPin, x);
        int newRightIndex = mBar.getNearestTickIndex(mScrollPin);
        final int componentLeft = getLeft() + getPaddingLeft();
        final int componentRight = getRight() - getPaddingRight() - componentLeft;
        if (x > componentLeft && x >= componentRight) {
            newRightIndex = getTickCount() - 1;
            movePin(mScrollPin, mBar.getRightX());
        }
        if (newRightIndex != mRightIndex) {
            mRightIndex = newRightIndex;
            if (mListener != null) mListener.onRangeChangeListener(this, mRightIndex, true);
        }
    }

    private void pressPin(final CircleView thumb) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, mExpandedPinRadius);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mThumbRadiusDP = (Float) (animation.getAnimatedValue());
                thumb.setSize(mPinPadding * animation.getAnimatedFraction());
                invalidate();
            }
        });
        animator.start();
        thumb.press();
    }

    private void releasePin(final CircleView thumb) {
        final float nearestTickX = mBar.getNearestTickCoordinate(thumb);
        thumb.setX(nearestTickX);
        ValueAnimator animator = ValueAnimator.ofFloat(mExpandedPinRadius, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mThumbRadiusDP = (Float) (animation.getAnimatedValue());
                thumb.setSize(mPinPadding - (mPinPadding * animation.getAnimatedFraction()));
                invalidate();
            }
        });
        animator.start();
        thumb.release();
    }

    private void movePin(CircleView thumb, float x) {
        if (x >= mBar.getLeftX() && x <= mBar.getRightX()) if (thumb != null) {
            thumb.setX(x);
            invalidate();
        }
    }

    public interface OnRangeBarChangeListener {
        void onRangeChangeListener(DistanceRangeBar distanceRangeBar, int rightPinIndex, boolean isMove);
    }

    private class Bar {
        private final Paint mBarPaint;
        private final Paint mTickPaint;
        private final Paint mEffectPaint;
        private final Paint mTickBoundaryPaint;
        private final Paint mTextPaint;
        private final float mLeftX;
        private final float mRightX;
        private final float mY;
        private int mNumSegments;
        private float mTickDistance;
        private final float mTickHeight;
        private List<Float> xCoordinateList = new LinkedList<>();

        Bar(float x, float y, float length) {

            mLeftX = x;
            mRightX = x + length;
            mY = y;
            mNumSegments = mTickCount - 1;
            mTickDistance = (length / mNumSegments) - (2 * mMargin) / mNumSegments;
            mTickHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mTickHeightDP, mContext.getResources().getDisplayMetrics());

            mBarPaint = new Paint();
            mBarPaint.setColor(mBarColor);
            mBarPaint.setStrokeWidth(mBarWeight);
            mBarPaint.setAntiAlias(true);
            mBarPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mTickPaint = new Paint();
            mTickPaint.setColor(mTickColor);
            mTickPaint.setStrokeWidth(mBarWeight);
            mTickPaint.setAntiAlias(true);
            mEffectPaint = new Paint();
            mEffectPaint.setStyle(Paint.Style.STROKE);
            mEffectPaint.setColor(mEffectColor);
            mEffectPaint.setAntiAlias(true);
            mEffectPaint.setStrokeWidth(2);
            mTickBoundaryPaint = new Paint();
            mTickBoundaryPaint.setStyle(Paint.Style.STROKE);
            mTickBoundaryPaint.setColor(mTickBoundaryColor);
            mTickBoundaryPaint.setStrokeWidth(mTickBoundaryWidth);
            mTickBoundaryPaint.setAntiAlias(true);
            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mBarTextSize, mContext.getResources().getDisplayMetrics()));
        }

        void draw(Canvas canvas) {
            RectF rectF = new RectF(mLeftX, mY, mRightX, mY);
            canvas.drawRoundRect(rectF, mBarWeight / 2, mBarWeight / 2, mBarPaint);

            Path path = new Path();
            path.moveTo(mLeftX + mMargin, mY);
            path.lineTo(mRightX - mMargin, mY);
            PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
            mEffectPaint.setPathEffect(effects);
            canvas.drawPath(path, mEffectPaint);
        }

        float getLeftX() {
            return mLeftX + mMargin;
        }

        float getRightX() {
            return mRightX - mMargin;
        }

        float getNearestTickCoordinate(CircleView thumb) {
            final int nearestTickIndex = getNearestTickIndex(thumb);
            return mLeftX + mMargin + (nearestTickIndex * mTickDistance);
        }

        int getNearestTickIndex(CircleView thumb) {
            return (int) ((thumb.getX() - mLeftX - mMargin + mTickDistance / 2f) / mTickDistance);
        }

        void drawTicks(Canvas canvas) {
            xCoordinateList.clear();
            for (int i = 0; i < mNumSegments; i++) {
                final float x = i * mTickDistance + mLeftX + mMargin;
                canvas.drawCircle(x, mY, mTickHeight, mTickBoundaryPaint);
                canvas.drawCircle(x, mY, mTickHeight, mTickPaint);
                xCoordinateList.add(x);
                if (!mList.isEmpty()) {
                    mTextPaint.setColor(i == mRightIndex ? mChooseTextColor : mUnChooseTextColor);
                    canvas.drawText(mList.get(i), x, mY + mBarTextMarginRangeBar, mTextPaint);
                }
            }
            canvas.drawCircle(mRightX - mMargin, mY, mTickHeight, mTickBoundaryPaint);
            canvas.drawCircle(mRightX - mMargin, mY, mTickHeight, mTickPaint);
            xCoordinateList.add(mRightX - mMargin);
            if (!mList.isEmpty()) {
                mTextPaint.setColor(mList.size() - 1 == mRightIndex ? mChooseTextColor : mUnChooseTextColor);
                canvas.drawText(mList.get(mList.size() - 1), mRightX - mMargin, mY + mBarTextMarginRangeBar, mTextPaint);
            }
        }

        List<Float> getXCoordinateList() {
            return xCoordinateList;
        }
    }

    class CircleView extends View {
        private static final float MINIMUM_TARGET_RADIUS_DP = 24;
        private float mTargetRadiusPx;
        private boolean mIsPressed = false;
        private float mY;
        private float mX;
        private float mPinPadding;
        private Paint mCirclePaint;
        private Paint mCircleBoundaryPaint;

        public CircleView(Context context, float y) {
            super(context);
            init(y);
        }

        private void init(float y) {
            mPinPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, mContext.getResources().getDisplayMetrics());
            mCirclePaint = new Paint();
            mCirclePaint.setColor(mCircleColor);
            mCirclePaint.setAntiAlias(true);
            if (mCircleBoundarySize != 0) {
                mCircleBoundaryPaint = new Paint();
                mCircleBoundaryPaint.setStyle(Paint.Style.STROKE);
                mCircleBoundaryPaint.setColor(mCircleBoundaryColor);
                mCircleBoundaryPaint.setStrokeWidth(mCircleBoundarySize);
                mCircleBoundaryPaint.setAntiAlias(true);
            }
            mTargetRadiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MINIMUM_TARGET_RADIUS_DP, mContext.getResources().getDisplayMetrics());
            mY = y;
        }

        @Override
        public void setX(float x) {
            mX = x;
        }

        @Override
        public float getX() {
            return mX;
        }

        @Override
        public boolean isPressed() {
            return mIsPressed;
        }

        public void press() {
            mIsPressed = true;
        }

        public void setSize(float padding) {
            mPinPadding = (int) padding;
            invalidate();
        }

        public void release() {
            mIsPressed = false;
        }

        public boolean isInTargetZone(float x, float y) {
            return (Math.abs(x - mX) <= mTargetRadiusPx
                    && Math.abs(y - mY + mPinPadding) <= mTargetRadiusPx);
        }

        @Override
        public void draw(Canvas canvas) {
            if (mCircleBoundaryPaint != null)
                canvas.drawCircle(mX, mY, mCircleSize, mCircleBoundaryPaint);
            canvas.drawCircle(mX, mY, mCircleSize, mCirclePaint);
            super.draw(canvas);
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                createPins();
                setRangePinsByIndices(mRightIndex);
            }
        }, 50);
    }
}
