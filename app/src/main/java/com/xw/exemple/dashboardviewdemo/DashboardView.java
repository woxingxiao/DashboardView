package com.xw.exemple.dashboardviewdemo;

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

public class DashboardView extends View {

    private int mRadius; // 圆弧半径
    private int mStartAngle; // 起始角度
    private int mSweepAngle; // 绘制角度
    private int mBigSliceCount; // 大份数
    private int mSliceCountInOneBigSlice; // 划分一大份长的小份数
    private int mArcColor; // 弧度颜色
    private int mMeasureTextSize; // 刻度字体大小
    private int mTextColor; // 字体颜色
    private int mTextColorHighLight; // 字体高亮颜色
    private String mHeaderTitle = ""; // 表头
    private int mHeaderTextSize; // 表头字体大小
    private int mHeaderRadius; // 表头半径
    private int mPointerRadius; // 指针半径
    private int mCircleRadius; // 中心圆半径
    private int mMinValue; // 最小值
    private int mMaxValue; // 最大值
    private float realTimeValue; // 实时值

    private int mBigSliceRadius; // 较长刻度半径
    private int mSmallSliceRadius; // 较短刻度半径
    private int mNumMeaRadius; // 数字刻度半径
    private int mViewWidth; // 控件宽度
    private int mViewHeight; // 控件高度
    private float mCenterX;
    private float mCenterY;

    private Paint mPaintArc;
    private Paint mPaintText;
    private Paint mPaintPointer;
    private Paint mPaintValue;

    private RectF mRectArc;
    private Rect mRectMeasures;
    private Rect mRectHeader;
    private Rect mRectRealText;
    private Path path;

    private int mSmallSliceCount; // 短刻度个数
    private float mBigSliceAngle; // 大刻度等分角度
    private float mSmallSliceAngle; // 小刻度等分角度

    private String[] mGraduations; // 6等分的刻度值
    private float initAngle;

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
        mTextColor = a.getColor(R.styleable.DashboardView_textColor, Color.WHITE);
        mTextColorHighLight = a.getColor(R.styleable.DashboardView_textColorHighLight, Color.parseColor("#f98907"));
        mHeaderTitle = a.getString(R.styleable.DashboardView_headerTitle);
        if (mHeaderTitle == null) mHeaderTitle = "";
        mHeaderTextSize = a.getDimensionPixelSize(R.styleable.DashboardView_headerTextSize, spToPx(14));
        mHeaderRadius = a.getDimensionPixelSize(R.styleable.DashboardView_headerRadius, mRadius / 3);
        mPointerRadius = a.getDimensionPixelSize(R.styleable.DashboardView_pointerRadius, mRadius / 3 * 2);
        mCircleRadius = a.getDimensionPixelSize(R.styleable.DashboardView_circleRadius, mRadius / 17);
        mMinValue = a.getInteger(R.styleable.DashboardView_minValue, 0);
        mMaxValue = a.getInteger(R.styleable.DashboardView_maxValue, 100);
        realTimeValue = a.getFloat(R.styleable.DashboardView_realTimeValue, 0.0f);

        a.recycle();

        if (mStartAngle + mSweepAngle > 360)
            throw new IllegalArgumentException("Sum of startAngle add sweepAngle must less than 360 degree");

        mSmallSliceRadius = mRadius - dpToPx(10);
        mBigSliceRadius = mSmallSliceRadius - dpToPx(8);
        mNumMeaRadius = mBigSliceRadius - dpToPx(2);

        mSmallSliceCount = mBigSliceCount * 5;
        mBigSliceAngle = mSweepAngle / (float) mBigSliceCount;
        mSmallSliceAngle = mBigSliceAngle / mSliceCountInOneBigSlice;
        mGraduations = getMeasureNumbers();

        if (mStartAngle <= 180 && mStartAngle + mSweepAngle >= 180) {
            mViewWidth = mRadius * 2 + getPaddingLeft() + getPaddingRight() + dpToPx(2) * 2;
        } else {
            float[] point1 = getCoordinatePoint(mRadius, mStartAngle);
            float[] point2 = getCoordinatePoint(mRadius, mStartAngle + mSweepAngle);
            float max = Math.max(Math.abs(point1[0]), Math.abs(point2[0]));
            mViewWidth = (int) (max * 2 + getPaddingLeft() + getPaddingRight() + dpToPx(2) * 2);
        }
        if ((mStartAngle <= 90 && mStartAngle + mSweepAngle >= 90) ||
                (mStartAngle <= 270 && mStartAngle + mSweepAngle >= 270)) {
            mViewHeight = mRadius * 2 + getPaddingLeft() + getPaddingRight() + dpToPx(2) * 2;
        } else {
            float[] point1 = getCoordinatePoint(mRadius, mStartAngle);
            float[] point2 = getCoordinatePoint(mRadius, mStartAngle + mSweepAngle);
            float max = Math.max(Math.abs(point1[0]), Math.abs(point2[0]));
            mViewHeight = (int) (max * 2 + getPaddingLeft() + getPaddingRight() + dpToPx(2) * 2);
        }

        mCenterX = mViewWidth / 2.0f;
        mCenterY = mViewHeight / 2.0f;

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
        mPaintArc = new Paint();
        mPaintArc.setAntiAlias(true);
        mPaintArc.setDither(true);
        mPaintArc.setColor(mArcColor);
        mPaintArc.setStyle(Paint.Style.STROKE);
        mPaintArc.setStrokeJoin(Paint.Join.ROUND);
        mPaintArc.setStrokeCap(Paint.Cap.ROUND);

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(mTextColor);
        mPaintText.setStyle(Paint.Style.STROKE);

        mPaintPointer = new Paint();
        mPaintPointer.setAntiAlias(true);
        mPaintPointer.setDither(true);

        mRectArc = new RectF(mCenterX - mRadius, mCenterY - mRadius, mCenterX + mRadius, mCenterY + mRadius);
        mRectMeasures = new Rect();
        mRectHeader = new Rect();
        mRectRealText = new Rect();

        mPaintValue = new Paint();
        mPaintValue.setAntiAlias(true);
        mPaintValue.setColor(mTextColor);
        mPaintValue.setStyle(Paint.Style.STROKE);
        mPaintValue.setTextAlign(Paint.Align.CENTER);
        mPaintValue.setTextSize(spToPx(16));
        mPaintValue.getTextBounds(trimFloat(realTimeValue), 0, trimFloat(realTimeValue).length(), mRectRealText);

        path = new Path();

        initAngle = getAngleFromResult(realTimeValue);
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
            if (mStartAngle >= 180 && mStartAngle + mSweepAngle <= 360) {
                mViewHeight = mRadius + mCircleRadius + dpToPx(2) + dpToPx(25) +
                        +getPaddingTop() + getPaddingBottom() + mRectRealText.height();
            }
            if (widthMode == MeasureSpec.AT_MOST)
                mViewHeight = Math.min(mViewHeight, widthSize);
        }

        setMeasuredDimension(mViewWidth, mViewHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //绘制刻度盘的弧形
        mPaintArc.setStrokeWidth(dpToPx(2));
        canvas.drawArc(mRectArc, mStartAngle, mSweepAngle, false, mPaintArc);

        //绘制刻度盘上的刻度
        for (int i = 0; i <= mBigSliceCount; i++) {
            float angle = i * mBigSliceAngle + mStartAngle;
            //绘制大刻度
            float[] point1 = getCoordinatePoint(mRadius, angle);
            float[] point2 = getCoordinatePoint(mBigSliceRadius, angle);
            canvas.drawLine(point1[0], point1[1], point2[0], point2[1], mPaintArc);

            //绘制圆盘上的数字
            mPaintText.setTextSize(mMeasureTextSize);
            String number = mGraduations[i];
            mPaintText.getTextBounds(number, 0, number.length(), mRectMeasures);
            if ((mBigSliceCount + 1) % 2 == 0) {
                if (i < (mBigSliceCount - 1) / 2) {
                    mPaintText.setTextAlign(Paint.Align.LEFT);
                } else if (i == (mBigSliceCount - 1) / 2 || i == (mBigSliceCount + 1) / 2) {
                    mPaintText.setTextAlign(Paint.Align.CENTER);
                } else {
                    mPaintText.setTextAlign(Paint.Align.RIGHT);
                }
            } else {
                if (i < mBigSliceCount / 2) {
                    mPaintText.setTextAlign(Paint.Align.LEFT);
                } else if (i == mBigSliceCount / 2) {
                    mPaintText.setTextAlign(Paint.Align.CENTER);
                } else {
                    mPaintText.setTextAlign(Paint.Align.RIGHT);
                }
            }
            float[] numberPoint = getCoordinatePoint(mNumMeaRadius, angle);
            if (i == 0 || i == mBigSliceCount) {
                canvas.drawText(number, numberPoint[0], numberPoint[1] + (mRectMeasures.height() / 2), mPaintText);
            } else {
                canvas.drawText(number, numberPoint[0], numberPoint[1] + mRectMeasures.height(), mPaintText);
            }
        }

        //绘制小的子刻度
        for (int i = 0; i < mSmallSliceCount; i++) {
            if (i % 5 != 0) {
                float angle = i * mSmallSliceAngle + mStartAngle;
                float[] point1 = getCoordinatePoint(mRadius, angle);
                float[] point2 = getCoordinatePoint(mSmallSliceRadius, angle);
                mPaintArc.setStrokeWidth(dpToPx(1));
                canvas.drawLine(point1[0], point1[1], point2[0], point2[1], mPaintArc);
            }
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
        mPaintPointer.setColor(Color.WHITE);
        canvas.drawCircle(mCenterX, mCenterY, mCircleRadius + dpToPx(2), mPaintPointer);

        //绘制三角形指针
        mPaintPointer.setStyle(Paint.Style.FILL);
        mPaintPointer.setColor(Color.WHITE);
        float[] point1 = getCoordinatePoint(dpToPx(3), initAngle + 90);
        path.moveTo(point1[0], point1[1]);
        float[] point2 = getCoordinatePoint(dpToPx(3), initAngle - 90);
        path.lineTo(point2[0], point2[1]);
        float[] point3 = getCoordinatePoint(mPointerRadius, initAngle);
        path.lineTo(point3[0], point3[1]);
        path.close();
        canvas.drawPath(path, mPaintPointer);

        // 绘制三角形指针底部的圆弧效果
        canvas.drawCircle((point1[0] + point2[0]) / 2, (point1[1] + point2[1]) / 2, dpToPx(3), mPaintPointer);
        // 绘制读数
        canvas.drawText(trimFloat(realTimeValue), mCenterX,
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
        return mSweepAngle * (result - mMinValue) / (mMaxValue - mMinValue) + mStartAngle;
    }

    public float getRealTimeValue() {
        return realTimeValue;
    }

    public void setRealTimeValue(float realTimeValue) {
        this.realTimeValue = realTimeValue;
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
}