package com.xw.sample.dashboardviewdemo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * DashboardView style 1
 * Created by XiaoWei on 2016-11-19.
 */

public class DashboardView1 extends View {

    private int mRadius;
    private int mStartAngle = 180; // 起始角度
    private int mSweepAngle = 180; // 绘制角度
    private int mMin = 0;
    private int mMax = 100;
    private int mSection = 10;
    private int mPortion = 10;
    private int mStrokeWidth;

    private Paint mPaint;
    private RectF mRectFArc;
    private Path mPath;
    private RectF mRectFInnerArc;

    public DashboardView1(Context context) {
        this(context, null);
    }

    public DashboardView1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashboardView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mStrokeWidth = dp2px(1);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mRectFArc = new RectF();
        mPath = new Path();
        mRectFInnerArc = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(resolveSize(dp2px(200), widthMeasureSpec),
                resolveSize(dp2px(200), widthMeasureSpec));

        mRadius = (getWidth() - getPaddingLeft() - getPaddingRight() - mStrokeWidth * 2) / 2;
        mRectFArc.set(
                getPaddingLeft() + mStrokeWidth,
                getPaddingTop() + mStrokeWidth,
                getWidth() - getPaddingRight() - mStrokeWidth,
                getHeight() - getPaddingBottom() - mStrokeWidth
        );
        mRectFInnerArc.set(
                getPaddingLeft() + mStrokeWidth + dp2px(18),
                getPaddingTop() + mStrokeWidth + dp2px(18),
                getWidth() - getPaddingRight() - mStrokeWidth - dp2px(18),
                getHeight() - getPaddingBottom() - mStrokeWidth - dp2px(18)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(Color.WHITE);
        canvas.drawArc(mRectFArc, mStartAngle, mSweepAngle, false, mPaint);

        double cos = Math.cos(Math.toRadians(mStartAngle - 180));
        double sin = Math.sin(Math.toRadians(mStartAngle - 180));
        float x0 = (float) (mRadius * (1 - cos) + mStrokeWidth + mStrokeWidth / 2f);
        float y0 = (float) (mRadius * (1 - sin) + mStrokeWidth);
        float x1 = (float) (mRadius - (mRadius - dp2px(10)) * cos + mStrokeWidth);
        float y1 = (float) (mRadius - (mRadius - dp2px(10)) * sin + mStrokeWidth);

        canvas.save();
        canvas.drawLine(x0, y0, x1, y1, mPaint);
        float degree = mSweepAngle * 1f / mSection;
        for (int i = 0; i < mSection; i++) {
            canvas.rotate(degree, getWidth() / 2f, getWidth() / 2f);
            canvas.drawLine(x0, y0, x1, y1, mPaint);
        }
        canvas.restore();

        canvas.save();
        mPaint.setStrokeWidth(1);
        float x2 = (float) (mRadius - (mRadius - dp2px(5)) * cos + mStrokeWidth + mStrokeWidth / 2f);
        float y2 = (float) (mRadius - (mRadius - dp2px(5)) * sin + mStrokeWidth);
        canvas.drawLine(x0, y0, x2, y2, mPaint);
        degree = mSweepAngle * 1f / (mSection * mPortion);
        for (int i = 0; i < mSection * mPortion; i++) {
            canvas.rotate(degree, getWidth() / 2f, getWidth() / 2f);
            canvas.drawLine(x0, y0, x2, y2, mPaint);
        }
        canvas.restore();

        String[] text = new String[]{"0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};
//        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(sp2px(10));
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < text.length; i++) {
            mPath.reset();
            mPath.addArc(mRectFInnerArc, mStartAngle + i * (mSweepAngle / mSection), mSweepAngle);
            canvas.drawTextOnPath(text[i], mPath, 0, 0, mPaint);
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }
}
