package com.xw.example.dashboardviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

/**
 * 仪表盘View
 *
 * @author woxingxiao
 */
public class DashboardView extends View {

    private int mRadius; // 圆弧半径
    private int mStartAngle; // 起始角度
    private int mSweepAngle; // 绘制角度
    private int mBigSliceCount; // 大份数
    private int mSliceCountInOneBigSlice; // 划分一大份长的小份数
    private int mArcColor; // 弧度颜色
    private int mMeasureTextSize; // 刻度字体大小
    private int mTextColor; // 字体颜色
    private String mHeaderTitle = ""; // 表头
    private int mHeaderTextSize; // 表头字体大小
    private int mHeaderRadius; // 表头半径
    private int mPointerRadius; // 指针半径
    private int mCircleRadius; // 中心圆半径
    private int mMinValue; // 最小值
    private int mMaxValue; // 最大值
    private float mRealTimeValue; // 实时值
    private int mStripeWidth; // 色条宽度
    private StripeMode mStripeMode = StripeMode.NORMAL;
    private int mBigSliceRadius; // 较长刻度半径
    private int mSmallSliceRadius; // 较短刻度半径
    private int mNumMeaRadius; // 数字刻度半径
    private int mModeType;
    private List<HighlightCR> mStripeHighlight; // 高亮范围颜色对象的集合
    private int mBgColor; // 背景色

    private int mViewWidth; // 控件宽度
    private int mViewHeight; // 控件高度
    private float mCenterX;
    private float mCenterY;

    private Paint mPaintArc;
    private Paint mPaintText;
    private Paint mPaintPointer;
    private Paint mPaintValue;
    private Paint mPaintStripe;

    private RectF mRectArc;
    private RectF mRectStripe;
    private Rect mRectMeasures;
    private Rect mRectHeader;
    private Rect mRectRealText;
    private Path path;

    private int mSmallSliceCount; // 短刻度个数
    private float mBigSliceAngle; // 大刻度等分角度
    private float mSmallSliceAngle; // 小刻度等分角度

    private String[] mGraduations; // 等分的刻度值
    private float initAngle;
    private boolean textColorFlag = true;

    public DashboardView(Context context) {
        this(context, null);
    }

    public DashboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DashboardView, defStyleAttr, 0);

        mRadius = a.getDimensionPixelSize(R.styleable.DashboardView_radius, dpToPx(80));
        mStartAngle = a.getInteger(R.styleable.DashboardView_startAngle, 180);
        mSweepAngle = a.getInteger(R.styleable.DashboardView_sweepAngle, 180);
        mBigSliceCount = a.getInteger(R.styleable.DashboardView_bigSliceCount, 5);
        mSliceCountInOneBigSlice = a.getInteger(R.styleable.DashboardView_sliceCountInOneBigSlice, 5);
        mArcColor = a.getColor(R.styleable.DashboardView_arcColor, Color.WHITE);
        mMeasureTextSize = a.getDimensionPixelSize(R.styleable.DashboardView_measureTextSize, spToPx(12));
        mTextColor = a.getColor(R.styleable.DashboardView_textColor, mArcColor);
        mHeaderTitle = a.getString(R.styleable.DashboardView_headerTitle);
        if (mHeaderTitle == null) mHeaderTitle = "";
        mHeaderTextSize = a.getDimensionPixelSize(R.styleable.DashboardView_headerTextSize, spToPx(14));
        mHeaderRadius = a.getDimensionPixelSize(R.styleable.DashboardView_headerRadius, mRadius / 3);
        mPointerRadius = a.getDimensionPixelSize(R.styleable.DashboardView_pointerRadius, mRadius / 3 * 2);
        mCircleRadius = a.getDimensionPixelSize(R.styleable.DashboardView_circleRadius, mRadius / 17);
        mMinValue = a.getInteger(R.styleable.DashboardView_minValue, 0);
        mMaxValue = a.getInteger(R.styleable.DashboardView_maxValue, 100);
        mRealTimeValue = a.getFloat(R.styleable.DashboardView_realTimeValue, 0.0f);
        mStripeWidth = a.getDimensionPixelSize(R.styleable.DashboardView_stripeWidth, 0);
        mModeType = a.getInt(R.styleable.DashboardView_stripeMode, 0);
        mBgColor = a.getColor(R.styleable.DashboardView_bgColor, 0);

        a.recycle();

        init();
    }

    private String[] getMeasureNumbers() {
        String[] strings = new String[mBigSliceCount + 1];
        for (int i = 0; i <= mBigSliceCount; i++) {
            if (i == 0) {
                strings[i] = String.valueOf(mMinValue);
            } else if (i == mBigSliceCount) {
                strings[i] = String.valueOf(mMaxValue);
            } else {
                strings[i] = String.valueOf(((mMaxValue - mMinValue) / mBigSliceCount) * i);
            }
        }

        return strings;
    }

    private void init() {
        if (mSweepAngle > 360)
            throw new IllegalArgumentException("sweepAngle must less than 360 degree");

        mSmallSliceRadius = mRadius - dpToPx(10);
        mBigSliceRadius = mSmallSliceRadius - dpToPx(8);
        mNumMeaRadius = mBigSliceRadius - dpToPx(2);

        mSmallSliceCount = mBigSliceCount * 5;
        mBigSliceAngle = mSweepAngle / (float) mBigSliceCount;
        mSmallSliceAngle = mBigSliceAngle / mSliceCountInOneBigSlice;
        mGraduations = getMeasureNumbers();

        switch (mModeType) {
            case 0:
                mStripeMode = StripeMode.NORMAL;
                break;
            case 1:
                mStripeMode = StripeMode.INNER;
                break;
            case 2:
                mStripeMode = StripeMode.OUTER;
                break;
        }

        int totalRadius;
        if (mStripeMode == StripeMode.OUTER) {
            totalRadius = mRadius + mStripeWidth;
        } else {
            totalRadius = mRadius;
        }

        mCenterX = mCenterY = 0.0f;
        if (mStartAngle <= 180 && mStartAngle + mSweepAngle >= 180) {
            mViewWidth = totalRadius * 2 + getPaddingLeft() + getPaddingRight() + dpToPx(2) * 2;
        } else {
            float[] point1 = getCoordinatePoint(totalRadius, mStartAngle);
            float[] point2 = getCoordinatePoint(totalRadius, mStartAngle + mSweepAngle);
            float max = Math.max(Math.abs(point1[0]), Math.abs(point2[0]));
            mViewWidth = (int) (max * 2 + getPaddingLeft() + getPaddingRight() + dpToPx(2) * 2);
        }
        if ((mStartAngle <= 90 && mStartAngle + mSweepAngle >= 90) ||
                (mStartAngle <= 270 && mStartAngle + mSweepAngle >= 270)) {
            mViewHeight = totalRadius * 2 + getPaddingLeft() + getPaddingRight() + dpToPx(2) * 2;
        } else {
            float[] point1 = getCoordinatePoint(totalRadius, mStartAngle);
            float[] point2 = getCoordinatePoint(totalRadius, mStartAngle + mSweepAngle);
            float max = Math.max(Math.abs(point1[1]), Math.abs(point2[1]));
            mViewHeight = (int) (max * 2 + getPaddingLeft() + getPaddingRight() + dpToPx(2) * 2);
        }

        mCenterX = mViewWidth / 2.0f;
        mCenterY = mViewHeight / 2.0f;

        mPaintArc = new Paint();
        mPaintArc.setAntiAlias(true);
        mPaintArc.setColor(mArcColor);
        mPaintArc.setStyle(Paint.Style.STROKE);
        mPaintArc.setStrokeCap(Paint.Cap.ROUND);

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(mTextColor);
        mPaintText.setStyle(Paint.Style.STROKE);

        mPaintPointer = new Paint();
        mPaintPointer.setAntiAlias(true);

        mPaintStripe = new Paint();
        mPaintStripe.setAntiAlias(true);
        mPaintStripe.setStyle(Paint.Style.STROKE);
        mPaintStripe.setStrokeWidth(mStripeWidth);

        mRectArc = new RectF(mCenterX - mRadius, mCenterY - mRadius, mCenterX + mRadius, mCenterY + mRadius);
        int r = 0;
        if (mStripeWidth > 0) {
            if (mStripeMode == StripeMode.OUTER) {
                r = mRadius + dpToPx(1) + mStripeWidth / 2;
            } else if (mStripeMode == StripeMode.INNER) {
                r = mRadius + dpToPx(1) - mStripeWidth / 2;
            }
            mRectStripe = new RectF(mCenterX - r, mCenterY - r, mCenterX + r, mCenterY + r);
        }
        mRectMeasures = new Rect();
        mRectHeader = new Rect();
        mRectRealText = new Rect();
        path = new Path();

        mPaintValue = new Paint();
        mPaintValue.setAntiAlias(true);
        mPaintValue.setColor(mTextColor);
        mPaintValue.setStyle(Paint.Style.STROKE);
        mPaintValue.setTextAlign(Paint.Align.CENTER);
        mPaintValue.setTextSize(Math.max(mHeaderTextSize, mMeasureTextSize));
        mPaintValue.getTextBounds(trimFloat(mRealTimeValue), 0, trimFloat(mRealTimeValue).length(), mRectRealText);

        initAngle = getAngleFromResult(mRealTimeValue);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mViewWidth = dpToPx(widthSize);
        } else {
            if (widthMode == MeasureSpec.AT_MOST)
                mViewWidth = Math.min(mViewWidth, widthSize);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mViewHeight = dpToPx(heightSize);
        } else {
            int totalRadius;
            if (mStripeMode == StripeMode.OUTER) {
                totalRadius = mRadius + mStripeWidth;
            } else {
                totalRadius = mRadius;
            }
            if (mStartAngle >= 180 && mStartAngle + mSweepAngle <= 360) {
                mViewHeight = totalRadius + mCircleRadius + dpToPx(2) + dpToPx(25) +
                        getPaddingTop() + getPaddingBottom() + mRectRealText.height();
            } else {
                float[] point1 = getCoordinatePoint(totalRadius, mStartAngle);
                float[] point2 = getCoordinatePoint(totalRadius, mStartAngle + mSweepAngle);
                float maxY = Math.max(Math.abs(point1[1]) - mCenterY, Math.abs(point2[1]) - mCenterY);
                float f = mCircleRadius + dpToPx(2) + dpToPx(25) + mRectRealText.height();
                float max = Math.max(maxY, f);
                mViewHeight = (int) (max + totalRadius + getPaddingTop() + getPaddingBottom() + dpToPx(2) * 2);
            }
            if (widthMode == MeasureSpec.AT_MOST)
                mViewHeight = Math.min(mViewHeight, widthSize);
        }

        setMeasuredDimension(mViewWidth, mViewHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mBgColor != 0)
            canvas.drawColor(mBgColor);

        // 绘制色带
        if (mStripeMode != StripeMode.NORMAL && mStripeHighlight != null) {
            for (int i = 0; i < mStripeHighlight.size(); i++) {
                HighlightCR highlightCR = mStripeHighlight.get(i);
                if (highlightCR.getColor() == 0 || highlightCR.getSweepAngle() == 0)
                    continue;

                mPaintStripe.setColor(highlightCR.getColor());
                if (highlightCR.getStartAngle() + highlightCR.getSweepAngle() <= mStartAngle + mSweepAngle) {
                    canvas.drawArc(mRectStripe, highlightCR.getStartAngle(),
                            highlightCR.getSweepAngle(), false, mPaintStripe);
                } else {
                    canvas.drawArc(mRectStripe, highlightCR.getStartAngle(),
                            mStartAngle + mSweepAngle - highlightCR.getStartAngle(), false, mPaintStripe);
                    break;
                }
            }
        }

        mPaintArc.setStrokeWidth(dpToPx(2));
        //绘制刻度盘
        for (int i = 0; i <= mBigSliceCount; i++) {
            //绘制大刻度
            float angle = i * mBigSliceAngle + mStartAngle;
            float[] point1 = getCoordinatePoint(mRadius, angle);
            float[] point2 = getCoordinatePoint(mBigSliceRadius, angle);

            if (mStripeMode == StripeMode.NORMAL && mStripeHighlight != null) {
                for (int j = 0; j < mStripeHighlight.size(); j++) {
                    HighlightCR highlightCR = mStripeHighlight.get(j);
                    if (highlightCR.getColor() == 0 || highlightCR.getSweepAngle() == 0)
                        continue;

                    if (angle <= highlightCR.getStartAngle() + highlightCR.getSweepAngle()) {
                        mPaintArc.setColor(highlightCR.getColor());
                        break;
                    } else {
                        mPaintArc.setColor(mArcColor);
                    }
                }
            } else {
                mPaintArc.setColor(mArcColor);
            }
            canvas.drawLine(point1[0], point1[1], point2[0], point2[1], mPaintArc);

            //绘制圆盘上的数字
            mPaintText.setTextSize(mMeasureTextSize);
            String number = mGraduations[i];
            mPaintText.getTextBounds(number, 0, number.length(), mRectMeasures);
            if (angle % 360 > 135 && angle % 360 < 215) {
                mPaintText.setTextAlign(Paint.Align.LEFT);
            } else if ((angle % 360 >= 0 && angle % 360 < 45) || (angle % 360 > 325 && angle % 360 <= 360)) {
                mPaintText.setTextAlign(Paint.Align.RIGHT);
            } else {
                mPaintText.setTextAlign(Paint.Align.CENTER);
            }
            float[] numberPoint = getCoordinatePoint(mNumMeaRadius, angle);
            if (i == 0 || i == mBigSliceCount) {
                canvas.drawText(number, numberPoint[0], numberPoint[1] + (mRectMeasures.height() / 2), mPaintText);
            } else {
                canvas.drawText(number, numberPoint[0], numberPoint[1] + mRectMeasures.height(), mPaintText);
            }
        }

        //绘制小的子刻度
        mPaintArc.setStrokeWidth(dpToPx(1));
        for (int i = 0; i < mSmallSliceCount; i++) {
            if (i % mSliceCountInOneBigSlice != 0) {
                float angle = i * mSmallSliceAngle + mStartAngle;
                float[] point1 = getCoordinatePoint(mRadius, angle);
                float[] point2 = getCoordinatePoint(mSmallSliceRadius, angle);

                if (mStripeMode == StripeMode.NORMAL && mStripeHighlight != null) {
                    for (int j = 0; j < mStripeHighlight.size(); j++) {
                        HighlightCR highlightCR = mStripeHighlight.get(j);
                        if (highlightCR.getColor() == 0 || highlightCR.getSweepAngle() == 0)
                            continue;

                        if (angle <= highlightCR.getStartAngle() + highlightCR.getSweepAngle()) {
                            mPaintArc.setColor(highlightCR.getColor());
                            break;
                        } else {
                            mPaintArc.setColor(mArcColor);
                        }
                    }
                } else {
                    mPaintArc.setColor(mArcColor);
                }
                mPaintArc.setStrokeWidth(dpToPx(1));
                canvas.drawLine(point1[0], point1[1], point2[0], point2[1], mPaintArc);
            }
        }

        //绘制刻度盘的弧形
        mPaintArc.setStrokeWidth(dpToPx(2));
        if (mStripeMode == StripeMode.NORMAL) {
            if (mStripeHighlight != null) {
                for (int i = 0; i < mStripeHighlight.size(); i++) {
                    HighlightCR highlightCR = mStripeHighlight.get(i);
                    if (highlightCR.getColor() == 0 || highlightCR.getSweepAngle() == 0)
                        continue;

                    mPaintArc.setColor(highlightCR.getColor());
                    if (highlightCR.getStartAngle() + highlightCR.getSweepAngle() <= mStartAngle + mSweepAngle) {
                        canvas.drawArc(mRectArc, highlightCR.getStartAngle(),
                                highlightCR.getSweepAngle(), false, mPaintArc);
                    } else {
                        canvas.drawArc(mRectArc, highlightCR.getStartAngle(),
                                mStartAngle + mSweepAngle - highlightCR.getStartAngle(), false, mPaintArc);
                        break;
                    }
                }
            } else {
                mPaintArc.setColor(mArcColor);
                canvas.drawArc(mRectArc, mStartAngle, mSweepAngle, false, mPaintArc);
            }
        } else if (mStripeMode == StripeMode.OUTER) {
            mPaintArc.setColor(mArcColor);
            canvas.drawArc(mRectArc, mStartAngle, mSweepAngle, false, mPaintArc);
        }

        //表头
        mPaintText.setTextSize(mHeaderTextSize);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.getTextBounds(mHeaderTitle, 0, mHeaderTitle.length(), mRectHeader);
        canvas.drawText(mHeaderTitle, mCenterX, mCenterY - mHeaderRadius + mRectHeader.height(), mPaintText);

        //绘制中心点的圆
        mPaintPointer.setStyle(Paint.Style.FILL);
        mPaintPointer.setColor(Color.parseColor("#e4e9e9"));
        canvas.drawCircle(mCenterX, mCenterY, mCircleRadius, mPaintPointer);

        mPaintPointer.setStyle(Paint.Style.STROKE);
        mPaintPointer.setStrokeWidth(dpToPx(4));
        mPaintPointer.setColor(mArcColor);
        canvas.drawCircle(mCenterX, mCenterY, mCircleRadius + dpToPx(2), mPaintPointer);

        //绘制三角形指针
        mPaintPointer.setStyle(Paint.Style.FILL);
        mPaintPointer.setColor(mArcColor);
        float[] point1 = getCoordinatePoint(mCircleRadius / 2, initAngle + 90);
        path.moveTo(point1[0], point1[1]);
        float[] point2 = getCoordinatePoint(mCircleRadius / 2, initAngle - 90);
        path.lineTo(point2[0], point2[1]);
        float[] point3 = getCoordinatePoint(mPointerRadius, initAngle);
        path.lineTo(point3[0], point3[1]);
        path.close();
        canvas.drawPath(path, mPaintPointer);
        // 绘制三角形指针底部的圆弧效果
        canvas.drawCircle((point1[0] + point2[0]) / 2, (point1[1] + point2[1]) / 2,
                mCircleRadius / 2, mPaintPointer);

        // 绘制读数
        canvas.drawText(trimFloat(mRealTimeValue), mCenterX,
                mCenterY + mCircleRadius + dpToPx(2) + dpToPx(25), mPaintValue);
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int spToPx(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    /**
     * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
     */
    public float[] getCoordinatePoint(int radius, float cirAngle) {
        float[] point = new float[2];

        double arcAngle = Math.toRadians(cirAngle); //将角度转换为弧度
        if (cirAngle < 90) {
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (cirAngle == 90) {
            point[0] = mCenterX;
            point[1] = mCenterY + radius;
        } else if (cirAngle > 90 && cirAngle < 180) {
            arcAngle = Math.PI * (180 - cirAngle) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (cirAngle == 180) {
            point[0] = mCenterX - radius;
            point[1] = mCenterY;
        } else if (cirAngle > 180 && cirAngle < 270) {
            arcAngle = Math.PI * (cirAngle - 180) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        } else if (cirAngle == 270) {
            point[0] = mCenterX;
            point[1] = mCenterY - radius;
        } else {
            arcAngle = Math.PI * (360 - cirAngle) / 180.0;
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        }

        return point;
    }

    /**
     * 通过数值得到角度位置
     */
    private float getAngleFromResult(float result) {
        if (result > mMaxValue)
            return mMaxValue;
        return mSweepAngle * (result - mMinValue) / (mMaxValue - mMinValue) + mStartAngle;
    }

    /**
     * float类型如果小数点后为零则显示整数否则保留
     */
    public static String trimFloat(float value) {
        if (Math.round(value) - value == 0) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int radius) {
        mRadius = dpToPx(radius);
        init();
    }

    public int getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(int startAngle) {
        mStartAngle = startAngle;
        init();
    }

    public int getSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(int sweepAngle) {
        mSweepAngle = sweepAngle;
        init();
    }

    public int getBigSliceCount() {
        return mBigSliceCount;
    }

    public void setBigSliceCount(int bigSliceCount) {
        mBigSliceCount = bigSliceCount;
        init();
    }

    public int getSliceCountInOneBigSlice() {
        return mSliceCountInOneBigSlice;
    }

    public void setSliceCountInOneBigSlice(int sliceCountInOneBigSlice) {
        mSliceCountInOneBigSlice = sliceCountInOneBigSlice;
        init();
    }

    public int getArcColor() {
        return mArcColor;
    }

    public void setArcColor(int arcColor) {
        mArcColor = arcColor;
        if (textColorFlag)
            mTextColor = mArcColor;
        init();
    }

    public int getMeasureTextSize() {
        return mMeasureTextSize;
    }

    public void setMeasureTextSize(int measureTextSize) {
        mMeasureTextSize = spToPx(measureTextSize);
        init();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        textColorFlag = false;
        init();
    }

    public String getHeaderTitle() {
        return mHeaderTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        mHeaderTitle = headerTitle;
        init();
    }

    public int getHeaderTextSize() {
        return mHeaderTextSize;
    }

    public void setHeaderTextSize(int headerTextSize) {
        mHeaderTextSize = spToPx(headerTextSize);
        init();
    }

    public int getHeaderRadius() {
        return mHeaderRadius;
    }

    public void setHeaderRadius(int headerRadius) {
        mHeaderRadius = dpToPx(headerRadius);
        init();
    }

    public int getPointerRadius() {
        return mPointerRadius;
    }

    public void setPointerRadius(int pointerRadius) {
        mPointerRadius = dpToPx(pointerRadius);
        init();
    }

    public int getCircleRadius() {
        return mCircleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        mCircleRadius = dpToPx(circleRadius);
        init();
    }

    public int getMinValue() {
        return mMinValue;
    }

    public void setMinValue(int minValue) {
        mMinValue = minValue;
        init();
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
        init();
    }

    public float getRealTimeValue() {
        return mRealTimeValue;
    }

    public void setRealTimeValue(float realTimeValue) {
        mRealTimeValue = realTimeValue;
        init();
    }

    public int getStripeWidth() {
        return mStripeWidth;
    }

    public void setStripeWidth(int stripeWidth) {
        mStripeWidth = dpToPx(stripeWidth);
        init();
    }

    public StripeMode getStripeMode() {
        return mStripeMode;
    }

    public void setStripeMode(StripeMode mStripeMode) {
        this.mStripeMode = mStripeMode;
        switch (mStripeMode) {
            case NORMAL:
                mModeType = 0;
                break;
            case INNER:
                mModeType = 1;
                break;
            case OUTER:
                mModeType = 2;
                break;
        }
        init();
    }

    public int getBigSliceRadius() {
        return mBigSliceRadius;
    }

    public void setBigSliceRadius(int bigSliceRadius) {
        mBigSliceRadius = dpToPx(bigSliceRadius);
        init();
    }

    public int getSmallSliceRadius() {
        return mSmallSliceRadius;
    }

    public void setSmallSliceRadius(int smallSliceRadius) {
        mSmallSliceRadius = dpToPx(smallSliceRadius);
        init();
    }

    public int getNumMeaRadius() {
        return mNumMeaRadius;
    }

    public void setNumMeaRadius(int numMeaRadius) {
        mNumMeaRadius = dpToPx(numMeaRadius);
        init();
    }

    public void setStripeHighlightColorAndRange(List<HighlightCR> stripeHighlight) {
        mStripeHighlight = stripeHighlight;
        init();
    }

    public enum StripeMode {
        NORMAL,
        INNER,
        OUTER
    }

    public int getBgColor() {
        return mBgColor;
    }

    public void setBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
        init();
    }
}