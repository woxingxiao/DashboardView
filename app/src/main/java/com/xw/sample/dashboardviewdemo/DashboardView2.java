package com.xw.sample.dashboardviewdemo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * DashboardView style 2
 * Created by woxingxiao on 2016-11-19.
 */

public class DashboardView2 extends View {

    private int mRadius; // 扇形半径
    private int mStartAngle = 150; // 起始角度
    private int mSweepAngle = 240; // 绘制角度
    private int mMin = 0; // 最小值
    private int mMax = 100; // 最大值
    private int mSection = 10; // 值域（mMax-mMin）等分份数
    private int mPortion = 3; // 一个mSection等分份数
    private String mHeaderText = "BETA"; // 表头
    private int mRealTimeValue = mMin; // 实时读数
    private int mSparkleWidth; // 亮点宽度
    private int mProgressWidth; // 进度圆弧宽度
    private int mLength1; // 刻度顶部相对边缘的长度
    private int mCalibrationWidth; // 刻度圆弧宽度
    private int mLength2; // 刻度读数顶部相对边缘的长度

    private int mPadding;
    private float mCenterX, mCenterY; // 圆心坐标
    private Paint mPaint;
    private RectF mRectFProgressArc;
    private RectF mRectFCalibrationFArc;
    private RectF mRectFTextArc;
    private Path mPath;
    private Rect mRectText;
    private String[] mTexts;

    public DashboardView2(Context context) {
        this(context, null);
    }

    public DashboardView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashboardView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mSparkleWidth = dp2px(6);
        mProgressWidth = dp2px(2);
        mCalibrationWidth = dp2px(10);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mRectFProgressArc = new RectF();
        mRectFCalibrationFArc = new RectF();
        mRectFTextArc = new RectF();
        mPath = new Path();
        mRectText = new Rect();

        mTexts = new String[]{"350", "较差", "550", "中等", "600", "良好", "650", "优秀", "700", "极好", "950"};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mPadding = Math.max(
                Math.max(getPaddingLeft(), getPaddingTop()),
                Math.max(getPaddingRight(), getPaddingBottom())
        );
        setPadding(mPadding, mPadding, mPadding, mPadding);

        mLength1 = mPadding + mCalibrationWidth / 2 + dp2px(5);
        mLength2 = mLength1 + mCalibrationWidth + dp2px(1) + dp2px(5);

        int width = resolveSize(dp2px(210), widthMeasureSpec);
        mRadius = (width - mPadding * 2 - mProgressWidth * 2) / 2;

        // 高度由信用值文字高度的一半（被圆心平分）+ 信用描述 + 评估时间 + 三行文字间各自间距 确定
        int height = mRadius + mProgressWidth * 2;
        mPaint.setTextSize(sp2px(40));
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);
        height += mRectText.height() / 2;

        mPaint.setTextSize(sp2px(30));
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);
        height += dp2px(16) + mRectText.height();

        mPaint.setTextSize(sp2px(10));
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);
        height += dp2px(8) + mRectText.height();

        setMeasuredDimension(width, height + getPaddingTop() + getPaddingBottom());

        mCenterX = mCenterY = getWidth() / 2f;
        mRectFProgressArc.set(
                mPadding + mSparkleWidth / 2,
                mPadding + mSparkleWidth / 2,
                getWidth() - mPadding - mSparkleWidth / 2,
                getWidth() - mPadding - mSparkleWidth / 2
        );

        mRectFCalibrationFArc.set(
                mLength1 + mCalibrationWidth / 2,
                mLength1 + mCalibrationWidth / 2,
                getWidth() - mLength1 - mCalibrationWidth / 2,
                getWidth() - mLength1 - mCalibrationWidth / 2
        );

        mPaint.setTextSize(sp2px(10));
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);
        mRectFTextArc.set(
                mLength2 + mRectText.height(),
                mLength2 + mRectText.height(),
                getWidth() - mLength2 - mRectText.height(),
                getWidth() - mLength2 - mRectText.height()
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(ContextCompat.getColor(getContext(), R.color.color_green));

        /**
         * 画进度圆弧
         */
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(100);
        canvas.drawArc(mRectFProgressArc, mStartAngle, mSweepAngle, false, mPaint);

        /**
         * 画刻度圆弧
         */
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setStrokeWidth(mCalibrationWidth);
        float α = (float) (180 * (mCalibrationWidth - dp2px(2)) / 2f /
                (Math.PI * (mRadius - mLength1 - (mCalibrationWidth - dp2px(2)) / 2f)));
        canvas.drawArc(mRectFCalibrationFArc, mStartAngle + α, mSweepAngle - 2 * α, false, mPaint);

        /**
         * 画长刻度
         * 画好起始角度的一条刻度后通过canvas绕着原点旋转来画剩下的长刻度
         */
        double cos = Math.cos(Math.toRadians(mStartAngle - 180));
        double sin = Math.sin(Math.toRadians(mStartAngle - 180));
        float x0 = (float) (mPadding + mRadius - (mRadius - mLength1 - dp2px(1)) * cos);
        float y0 = (float) (mPadding + mRadius + dp2px(2) - (mRadius - mLength1) * sin);
        float x1 = (float) (mPadding + mRadius - (mRadius - mLength1 - mCalibrationWidth - dp2px(1)) * cos);
        float y1 = (float) (mPadding + mRadius + dp2px(2) - (mRadius - mLength1 - mCalibrationWidth) * sin);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(dp2px(2));
        mPaint.setAlpha(120);
        canvas.save();
        canvas.drawLine(x0, y0, x1, y1, mPaint);
        float angle = mSweepAngle * 1f / mSection;
        for (int i = 0; i < mSection; i++) {
            canvas.rotate(angle, mCenterX, mCenterY);
            canvas.drawLine(x0, y0, x1, y1, mPaint);
        }
        canvas.restore();

        /**
         * 画短刻度
         * 同样采用canvas的旋转原理
         */
        canvas.save();
        mPaint.setStrokeWidth(dp2px(1));
        mPaint.setAlpha(100);
        float x2 = (float) (mPadding + mRadius - (mRadius - mLength1 - mCalibrationWidth) * cos);
        float y2 = (float) (mPadding + mRadius + dp2px(2) - (mRadius - mLength1 - mCalibrationWidth) * sin);
        canvas.drawLine(x0, y0, x2, y2, mPaint);
        angle = mSweepAngle * 1f / (mSection * mPortion);
        for (int i = 1; i < mSection * mPortion; i++) {
            canvas.rotate(angle, mCenterX, mCenterY);
            if (i % mPortion == 0) {
                continue;
            }
            canvas.drawLine(x0, y0, x2, y2, mPaint);
        }
        canvas.restore();

        /**
         * 画长刻度读数
         * 添加一个圆弧path，文字沿着path绘制
         */
        mPaint.setTextSize(sp2px(10));
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAlpha(150);
        for (int i = 0; i < mTexts.length; i++) {
            mPaint.getTextBounds(mTexts[i], 0, mTexts[i].length(), mRectText);
            // 粗略把文字的宽度视为圆心角2*θ对应的弧长，利用弧长公式得到θ，下面用于修正角度
            float θ = (float) (180 * mRectText.width() / 2 /
                    (Math.PI * (mRadius - mLength2 - mRectText.height())));

            mPath.reset();
            mPath.addArc(
                    mRectFTextArc,
                    mStartAngle + i * (mSweepAngle / mSection) - θ, // 正起始角度减去θ使文字居中对准长刻度
                    mSweepAngle
            );
            canvas.drawTextOnPath(mTexts[i], mPath, 0, 0, mPaint);
        }

        /**
         * 画实时度数值
         */
        mPaint.setAlpha(255);
        mPaint.setTextSize(sp2px(45));
        mPaint.setTextAlign(Paint.Align.CENTER);
        String value = String.valueOf(610);
        mPaint.getTextBounds(value, 0, value.length(), mRectText);
        float h = mRectText.height() / 2f;
        canvas.drawText(value, mCenterX, mCenterY + h, mPaint);
        h = mRectText.height() / 2f;

        /**
         * 画表头
         */
        mPaint.setAlpha(150);
        mPaint.setTextSize(sp2px(12));
        canvas.drawText(mHeaderText, mCenterX, mCenterY - h - dp2px(10), mPaint);

        /**
         * 画信用描述
         */
        mPaint.setAlpha(255);
        mPaint.setTextSize(sp2px(20));
        mPaint.getTextBounds(value, 0, value.length(), mRectText);
        h += dp2px(15) + mRectText.height() / 2f;
        canvas.drawText("信用良好", mCenterX, mCenterY + h, mPaint);
        h += mRectText.height() / 2f;

        /**
         * 画评估时间
         */
        mPaint.setAlpha(150);
        mPaint.setTextSize(sp2px(10));
        mPaint.getTextBounds(value, 0, value.length(), mRectText);
        h += dp2px(5) + mRectText.height() / 2f;
        canvas.drawText("评估时间:2016.11.23", mCenterX, mCenterY + h, mPaint);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    public float[] getCoordinatePoint(int radius, float angle) {
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

    public int getRealTimeValue() {
        return mRealTimeValue;
    }

    public void setRealTimeValue(int realTimeValue) {
        if (mRealTimeValue == realTimeValue || realTimeValue < mMin || realTimeValue > mMax) {
            return;
        }

        mRealTimeValue = realTimeValue;
        postInvalidate();
    }
}
