package com.xw.sample.dashboardviewdemo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DashboardView style 3 仿最新版芝麻信用分
 * Created by woxingxiao on 2016-11-28.
 */

public class DashboardView3 extends View {

    private int mRadius; // 画布边缘半径（去除padding后的半径）
    private int mStartAngle = 150; // 起始角度
    private int mSweepAngle = 240; // 绘制角度
    private int mMin = 350; // 最小值
    private int mMax = 950; // 最大值
    private String mHeaderText = "BETA"; // 表头
    private int mCreditValue = 782; // 信用分
    private int mSparkleWidth; // 亮点宽度
    private int mProgressWidth; // 进度圆弧宽度
    private float mLength1; // 刻度顶部相对边缘的长度
    private float mLength2; // 信用值指示器顶部相对边缘的长度

    private int mPadding;
    private float mCenterX, mCenterY; // 圆心坐标
    private Paint mPaint;
    private RectF mRectFProgressArc;
    private Rect mRectText;
    private Path mPath;
    private int[] mBgColors;

    public DashboardView3(Context context) {
        this(context, null);
    }

    public DashboardView3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashboardView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mSparkleWidth = dp2px(10);
        mProgressWidth = 4;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setColor(Color.WHITE);

        mRectFProgressArc = new RectF();
        mRectText = new Rect();
        mPath = new Path();

        mBgColors = new int[]{ContextCompat.getColor(getContext(), R.color.color_red),
                ContextCompat.getColor(getContext(), R.color.color_orange),
                ContextCompat.getColor(getContext(), R.color.color_yellow),
                ContextCompat.getColor(getContext(), R.color.color_green),
                ContextCompat.getColor(getContext(), R.color.color_blue)};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mPadding = Math.max(
                Math.max(getPaddingLeft(), getPaddingTop()),
                Math.max(getPaddingRight(), getPaddingBottom())
        );
        setPadding(mPadding, mPadding, mPadding, mPadding);

        mLength1 = mPadding + mSparkleWidth / 2f + dp2px(8);
        mLength2 = mLength1 + mProgressWidth + dp2px(4);

        int width = resolveSize(dp2px(220), widthMeasureSpec);
        mRadius = (width - mPadding * 2) / 2;

        setMeasuredDimension(width, width - dp2px(50));

        mCenterX = mCenterY = getMeasuredWidth() / 2f;
        mRectFProgressArc.set(
                mPadding + mSparkleWidth / 2f,
                mPadding + mSparkleWidth / 2f,
                getMeasuredWidth() - mPadding - mSparkleWidth / 2f,
                getMeasuredWidth() - mPadding - mSparkleWidth / 2f
        );

        mPaint.setTextSize(sp2px(10));
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(calculateBGColorWithValue(mCreditValue));

        /**
         * 画进度圆弧背景
         */
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setAlpha(80);
        canvas.drawArc(mRectFProgressArc, mStartAngle, mSweepAngle, false, mPaint);

        mPaint.setAlpha(255);
        /**
         * 画进度圆弧(起始到信用值)
         */
        mPaint.setShader(generateSweepGradient());
        canvas.drawArc(mRectFProgressArc, mStartAngle,
                calculateRelativeAngleWithValue(mCreditValue), false, mPaint);
        /**
         * 画信用值指示亮点
         */
        float[] point = getCoordinatePoint(
                mRadius - mSparkleWidth / 2f,
                mStartAngle + calculateRelativeAngleWithValue(mCreditValue)
        );
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(generateRadialGradient(point[0], point[1]));
        canvas.drawCircle(point[0], point[1], mSparkleWidth / 2f, mPaint);

        /**
         * 画刻度
         */
        int cnt = (mMax - mMin) / 2 / 10;
        float degree = mSweepAngle / ((mMax - mMin) / 10);
        float a = calculateRelativeAngleWithValue(mCreditValue);
        float b = mSweepAngle / 2f;
        mPaint.setShader(null);
        mPaint.setAlpha(a >= b ? 200 : 80);
        canvas.save();
        canvas.drawLine(mCenterX, mPadding + mLength1, mCenterX, mPadding + mLength1 - 1, mPaint);
        // 逆时针旋转
        for (int i = 0; i < cnt; i++) {
            canvas.rotate(-degree, mCenterX, mCenterY);
            b -= degree;
            mPaint.setAlpha(a >= b ? 200 : 80);
            canvas.drawLine(mCenterX, mPadding + mLength1, mCenterX, mPadding + mLength1 - 1, mPaint);
        }
        canvas.restore();
        canvas.save();
        // 顺时针旋转
        b = mSweepAngle / 2f;
        for (int i = 0; i < cnt; i++) {
            canvas.rotate(degree, mCenterX, mCenterY);
            b += degree;
            mPaint.setAlpha(a >= b ? 200 : 80);
            canvas.drawLine(mCenterX, mPadding + mLength1, mCenterX, mPadding + mLength1 - 1, mPaint);
        }
        canvas.restore();

        /**
         * 画信用分指示器
         */
        canvas.save();
        b = mSweepAngle / 2f;
        canvas.rotate(a - b, mCenterX, mCenterY);
        mPaint.setAlpha(255);
        mPaint.setStyle(Paint.Style.FILL);
        mPath.reset();
        mPath.moveTo(mCenterX, mPadding + mLength2);
        mPath.rLineTo(-dp2px(2), dp2px(5));
        mPath.rLineTo(dp2px(4), 0);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
        mPaint.setStrokeWidth(dp2px(1));
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mCenterX, mPadding + mLength2 + dp2px(6) + 1, dp2px(2), mPaint);
        canvas.restore();

        /**
         * 画实时度数值
         */
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(sp2px(35));
        mPaint.setTextAlign(Paint.Align.CENTER);
        String value = String.valueOf(mCreditValue);
        canvas.drawText(value, mCenterX, mCenterY, mPaint);

        /**
         * 画信用描述
         */
        mPaint.setTextSize(sp2px(14));
        canvas.drawText(calculateCreditDescription(), mCenterX, mCenterY - dp2px(40), mPaint);

        /**
         * 画表头
         */
        mPaint.setAlpha(160);
        mPaint.setTextSize(sp2px(10));
        canvas.drawText(mHeaderText, mCenterX, mCenterY - dp2px(65), mPaint);

        /**
         * 画评估时间
         */
        mPaint.setAlpha(160);
        mPaint.setTextSize(sp2px(9));
        canvas.drawText(getFormatTimeStr(), mCenterX, mCenterY + dp2px(25), mPaint);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    private SweepGradient generateSweepGradient() {
        SweepGradient sweepGradient = new SweepGradient(mCenterX, mCenterY,
                new int[]{Color.argb(0, 255, 255, 255), Color.argb(200, 255, 255, 255)},
                new float[]{0, calculateRelativeAngleWithValue(mCreditValue) / 360}
        );
        Matrix matrix = new Matrix();
        matrix.setRotate(mStartAngle - 1, mCenterX, mCenterY);
        sweepGradient.setLocalMatrix(matrix);

        return sweepGradient;
    }

    private RadialGradient generateRadialGradient(float x, float y) {
        return new RadialGradient(x, y, mSparkleWidth / 2f,
                new int[]{Color.argb(255, 255, 255, 255), Color.argb(80, 255, 255, 255)},
                new float[]{0.4f, 1},
                Shader.TileMode.CLAMP
        );
    }

    private float[] getCoordinatePoint(float radius, float angle) {
        float[] point = new float[2];

        double arcAngle = Math.toRadians(angle); //将角度转换为弧度
        if (angle < 90) {
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 90) {
            point[0] = mCenterX;
            point[1] = mCenterY + radius;
        } else if (angle > 90 && angle < 180) {
            arcAngle = Math.PI * (180 - angle) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 180) {
            point[0] = mCenterX - radius;
            point[1] = mCenterY;
        } else if (angle > 180 && angle < 270) {
            arcAngle = Math.PI * (angle - 180) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        } else if (angle == 270) {
            point[0] = mCenterX;
            point[1] = mCenterY - radius;
        } else {
            arcAngle = Math.PI * (360 - angle) / 180.0;
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        }

        return point;
    }

    /**
     * 相对起始角度计算信用分所对应的角度大小
     */
    private float calculateRelativeAngleWithValue(int value) {
        return mSweepAngle * value * 1f / mMax;
    }

    /**
     * 信用分对应信用描述
     */
    private String calculateCreditDescription() {
        if (mCreditValue > 700) {
            return "信用极好";
        } else if (mCreditValue > 650) {
            return "信用优秀";
        } else if (mCreditValue > 600) {
            return "信用良好";
        } else if (mCreditValue > 550) {
            return "信用中等";
        }
        return "信用较差";
    }

    /**
     * 信用分对应信用描述
     */
    private int calculateBGColorWithValue(int value) {
        if (value > 700) {
            return mBgColors[4];
        } else if (value > 650) {
            return mBgColors[3];
        } else if (value > 600) {
            return mBgColors[2];
        } else if (value > 550) {
            return mBgColors[1];
        }
        return mBgColors[0];
    }

    private SimpleDateFormat mDateFormat;

    private String getFormatTimeStr() {
        if (mDateFormat == null) {
            mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        }
        return String.format("评估时间:%s", mDateFormat.format(new Date()));
    }

    public int getCreditValue() {
        return mCreditValue;
    }

    /**
     * 设置信用值
     *
     * @param creditValue 信用值
     */
    public void setCreditValue(int creditValue) {
        if (mCreditValue == creditValue || creditValue < mMin || creditValue > mMax) {
            return;
        }

        mCreditValue = creditValue;
        postInvalidate();
    }

}
