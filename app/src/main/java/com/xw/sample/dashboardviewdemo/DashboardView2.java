package com.xw.sample.dashboardviewdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
 * DashboardView style 2 仿芝麻信用分
 * Created by woxingxiao on 2016-11-19.
 */

public class DashboardView2 extends View {

    private int mRadius; // 画布边缘半径（去除padding后的半径）
    private int mStartAngle = 150; // 起始角度
    private int mSweepAngle = 240; // 绘制角度
    private int mMin = 350; // 最小值
    private int mMax = 950; // 最大值
    private int mSection = 10; // 值域（mMax-mMin）等分份数
    private int mPortion = 3; // 一个mSection等分份数
    private String mHeaderText = "BETA"; // 表头
    private int mCreditValue = 650; // 信用分
    private int mSolidCreditValue = mCreditValue; // 信用分(设定后不变)
    private int mSparkleWidth; // 亮点宽度
    private int mProgressWidth; // 进度圆弧宽度
    private float mLength1; // 刻度顶部相对边缘的长度
    private int mCalibrationWidth; // 刻度圆弧宽度
    private float mLength2; // 刻度读数顶部相对边缘的长度

    private int mPadding;
    private float mCenterX, mCenterY; // 圆心坐标
    private Paint mPaint;
    private RectF mRectFProgressArc;
    private RectF mRectFCalibrationFArc;
    private RectF mRectFTextArc;
    private Path mPath;
    private Rect mRectText;
    private String[] mTexts;
    private int mBackgroundColor;
    private int[] mBgColors;
    /**
     * 由于真实的芝麻信用界面信用值不是线性排布，所以播放动画时若以信用值为参考，则会出现忽慢忽快
     * 的情况（开始以为是卡顿）。因此，先计算出最终到达角度，以扫过的角度为线性参考，动画就流畅了
     */
    private boolean isAnimFinish = true;
    private float mAngleWhenAnim;

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
        mSparkleWidth = dp2px(10);
        mProgressWidth = dp2px(3);
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
        mBgColors = new int[]{ContextCompat.getColor(getContext(), R.color.color_red),
                ContextCompat.getColor(getContext(), R.color.color_orange),
                ContextCompat.getColor(getContext(), R.color.color_yellow),
                ContextCompat.getColor(getContext(), R.color.color_green),
                ContextCompat.getColor(getContext(), R.color.color_blue)};
        mBackgroundColor = mBgColors[0];
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
        mLength2 = mLength1 + mCalibrationWidth + dp2px(1) + dp2px(5);

        int width = resolveSize(dp2px(220), widthMeasureSpec);
        mRadius = (width - mPadding * 2) / 2;

        setMeasuredDimension(width, width - dp2px(30));

        mCenterX = mCenterY = getMeasuredWidth() / 2f;
        mRectFProgressArc.set(
                mPadding + mSparkleWidth / 2f,
                mPadding + mSparkleWidth / 2f,
                getMeasuredWidth() - mPadding - mSparkleWidth / 2f,
                getMeasuredWidth() - mPadding - mSparkleWidth / 2f
        );

        mRectFCalibrationFArc.set(
                mLength1 + mCalibrationWidth / 2f,
                mLength1 + mCalibrationWidth / 2f,
                getMeasuredWidth() - mLength1 - mCalibrationWidth / 2f,
                getMeasuredWidth() - mLength1 - mCalibrationWidth / 2f
        );

        mPaint.setTextSize(sp2px(10));
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);
        mRectFTextArc.set(
                mLength2 + mRectText.height(),
                mLength2 + mRectText.height(),
                getMeasuredWidth() - mLength2 - mRectText.height(),
                getMeasuredWidth() - mLength2 - mRectText.height()
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(mBackgroundColor);

        /**
         * 画进度圆弧背景
         */
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setAlpha(80);
        canvas.drawArc(mRectFProgressArc, mStartAngle + 1, mSweepAngle - 2, false, mPaint);

        mPaint.setAlpha(255);
        if (isAnimFinish) {
            /**
             * 画进度圆弧(起始到信用值)
             */
            mPaint.setShader(generateSweepGradient());
            canvas.drawArc(mRectFProgressArc, mStartAngle + 1,
                    calculateRelativeAngleWithValue(mCreditValue) - 2, false, mPaint);
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
        } else {
            /**
             * 画进度圆弧(起始到信用值)
             */
            mPaint.setShader(generateSweepGradient());
            canvas.drawArc(mRectFProgressArc, mStartAngle + 1,
                    mAngleWhenAnim - mStartAngle - 2, false, mPaint);
            /**
             * 画信用值指示亮点
             */
            float[] point = getCoordinatePoint(
                    mRadius - mSparkleWidth / 2f,
                    mAngleWhenAnim
            );
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setShader(generateRadialGradient(point[0], point[1]));
            canvas.drawCircle(point[0], point[1], mSparkleWidth / 2f, mPaint);
        }

        /**
         * 画刻度圆弧
         */
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(80);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setStrokeWidth(mCalibrationWidth);
        canvas.drawArc(mRectFCalibrationFArc, mStartAngle + 3, mSweepAngle - 6, false, mPaint);

        /**
         * 画长刻度
         * 画好起始角度的一条刻度后通过canvas绕着原点旋转来画剩下的长刻度
         */
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(dp2px(2));
        mPaint.setAlpha(120);
        float x0 = mCenterX;
        float y0 = mPadding + mLength1 + dp2px(1);
        float x1 = mCenterX;
        float y1 = y0 + mCalibrationWidth;
        // 逆时针到开始处
        canvas.save();
        canvas.drawLine(x0, y0, x1, y1, mPaint);
        float degree = mSweepAngle / mSection;
        for (int i = 0; i < mSection / 2; i++) {
            canvas.rotate(-degree, mCenterX, mCenterY);
            canvas.drawLine(x0, y0, x1, y1, mPaint);
        }
        canvas.restore();
        // 顺时针到结尾处
        canvas.save();
        for (int i = 0; i < mSection / 2; i++) {
            canvas.rotate(degree, mCenterX, mCenterY);
            canvas.drawLine(x0, y0, x1, y1, mPaint);
        }
        canvas.restore();

        /**
         * 画短刻度
         * 同样采用canvas的旋转原理
         */
        mPaint.setStrokeWidth(dp2px(1));
        mPaint.setAlpha(80);
        float x2 = mCenterX;
        float y2 = y0 + mCalibrationWidth - dp2px(2);
        // 逆时针到开始处
        canvas.save();
        canvas.drawLine(x0, y0, x2, y2, mPaint);
        degree = mSweepAngle / (mSection * mPortion);
        for (int i = 0; i < (mSection * mPortion) / 2; i++) {
            canvas.rotate(-degree, mCenterX, mCenterY);
            canvas.drawLine(x0, y0, x2, y2, mPaint);
        }
        canvas.restore();
        // 顺时针到结尾处
        canvas.save();
        for (int i = 0; i < (mSection * mPortion) / 2; i++) {
            canvas.rotate(degree, mCenterX, mCenterY);
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
        mPaint.setAlpha(160);
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
        mPaint.setTextSize(sp2px(50));
        mPaint.setTextAlign(Paint.Align.CENTER);
        String value = String.valueOf(mSolidCreditValue);
        canvas.drawText(value, mCenterX, mCenterY + dp2px(30), mPaint);

        /**
         * 画表头
         */
        mPaint.setAlpha(160);
        mPaint.setTextSize(sp2px(12));
        canvas.drawText(mHeaderText, mCenterX, mCenterY - dp2px(20), mPaint);

        /**
         * 画信用描述
         */
        mPaint.setAlpha(255);
        mPaint.setTextSize(sp2px(20));
        canvas.drawText(calculateCreditDescription(), mCenterX, mCenterY + dp2px(55), mPaint);

        /**
         * 画评估时间
         */
        mPaint.setAlpha(160);
        mPaint.setTextSize(sp2px(10));
        canvas.drawText(getFormatTimeStr(), mCenterX, mCenterY + dp2px(70), mPaint);
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
        float degreePerSection = 1f * mSweepAngle / mSection;
        if (value > 700) {
            return 8 * degreePerSection + 2 * degreePerSection / 250 * (value - 700);
        } else if (value > 650) {
            return 6 * degreePerSection + 2 * degreePerSection / 50 * (value - 650);
        } else if (value > 600) {
            return 4 * degreePerSection + 2 * degreePerSection / 50 * (value - 600);
        } else if (value > 550) {
            return 2 * degreePerSection + 2 * degreePerSection / 50 * (value - 550);
        } else {
            return 2 * degreePerSection / 200 * (value - 350);
        }
    }

    /**
     * 信用分对应信用描述
     */
    private String calculateCreditDescription() {
        if (mSolidCreditValue > 700) {
            return "信用极好";
        } else if (mSolidCreditValue > 650) {
            return "信用优秀";
        } else if (mSolidCreditValue > 600) {
            return "信用良好";
        } else if (mSolidCreditValue > 550) {
            return "信用中等";
        }
        return "信用较差";
    }

    private SimpleDateFormat mDateFormat;

    private String getFormatTimeStr() {
        if (mDateFormat == null) {
            mDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
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
        if (mSolidCreditValue == creditValue || creditValue < mMin || creditValue > mMax) {
            return;
        }

        mSolidCreditValue = creditValue;
        mCreditValue = creditValue;
        postInvalidate();
    }

    /**
     * 设置信用值并播放动画
     *
     * @param creditValue 信用值
     */
    public void setCreditValueWithAnim(int creditValue) {
        if (creditValue < mMin || creditValue > mMax || !isAnimFinish) {
            return;
        }

        mSolidCreditValue = creditValue;

        ValueAnimator creditValueAnimator = ValueAnimator.ofInt(350, mSolidCreditValue);
        creditValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCreditValue = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        // 计算最终值对应的角度，以扫过的角度的线性变化来播放动画
        float degree = calculateRelativeAngleWithValue(mSolidCreditValue);

        ValueAnimator degreeValueAnimator = ValueAnimator.ofFloat(mStartAngle, mStartAngle + degree);
        degreeValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAngleWhenAnim = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator colorAnimator = ObjectAnimator.ofInt(this, "mBackgroundColor", mBgColors[0], mBgColors[0]);
        // 实时信用值对应的背景色的变化
        long delay = 1000;
        if (mSolidCreditValue > 700) {
            colorAnimator.setIntValues(mBgColors[0], mBgColors[1], mBgColors[2], mBgColors[3], mBgColors[4]);
            delay = 3000;
        } else if (mSolidCreditValue > 650) {
            colorAnimator.setIntValues(mBgColors[0], mBgColors[1], mBgColors[2], mBgColors[3]);
            delay = 2500;
        } else if (mSolidCreditValue > 600) {
            colorAnimator.setIntValues(mBgColors[0], mBgColors[1], mBgColors[2]);
            delay = 2000;
        } else if (mSolidCreditValue > 550) {
            colorAnimator.setIntValues(mBgColors[0], mBgColors[1]);
            delay = 1500;
        }
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBackgroundColor = (int) animation.getAnimatedValue();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .setDuration(delay)
                .playTogether(creditValueAnimator, degreeValueAnimator, colorAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimFinish = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimFinish = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                isAnimFinish = true;
            }
        });
        animatorSet.start();
    }

}
