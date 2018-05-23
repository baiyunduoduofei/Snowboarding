package com.csxm.snowboarding;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * @author: zhouyunfei
 * @date: 2018/5/23
 * @desc:
 */
public class SnowboardingView extends View {

    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mStartX;
    private int mStartY;
    private Path mSnowPath;//雪道path
    private Path mBottomPath;//底部path
    private static final float NUM_ON_SCREEN = 0.8f;//屏幕上有多少个波长
    private int mCycleLength = 0;//一个周期的长度
    protected float mFraction;
    private Bitmap mBitmap;
    private PathMeasure mPathMeasure;
    private float[] pos;
    private float[] tan;
    private Matrix mMatrix;
    ValueAnimator mAnimator;

    public SnowboardingView(Context context) {
        super(context);
        init();
    }

    public SnowboardingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SnowboardingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.snow_boarding, options);
        pos = new float[2];
        tan = new float[2];
        mMatrix = new Matrix();
        mSnowPath = new Path();
        mBottomPath = new Path();
        mPathMeasure = new PathMeasure();
    }

    /**
     * 开启动画
     */
    public void startAnimator() {

        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(8000);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFraction = (float) animation.getAnimatedValue();

                postInvalidate();
            }
        });
        mAnimator.start();

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCycleLength = (int) (w / NUM_ON_SCREEN);
        mStartY = (h / 2);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDraw(Canvas canvas) {
        mSnowPath.reset();
        mBottomPath.reset();
        mStartX = (int) (-mCycleLength * mFraction);
        mSnowPath.moveTo(mStartX, mStartY);
        for (int i = -1; i < NUM_ON_SCREEN; i++) {
            mSnowPath.rQuadTo(mCycleLength / 3 / 2, -mCycleLength / 3 / 2, mCycleLength / 3, 0);
            mSnowPath.rLineTo(mCycleLength / 3, 0);
            mSnowPath.rQuadTo(mCycleLength / 3 / 2 / 2, -mCycleLength / 3, mCycleLength / 3 / 2, 0);
            mSnowPath.rLineTo(mCycleLength / 3 / 2, 0);
        }

        mBottomPath.moveTo(mStartX, mStartY);
        mBottomPath.lineTo(0, mHeight);
        mBottomPath.lineTo(mWidth, mHeight);
        mBottomPath.lineTo(mStartX + mCycleLength * (NUM_ON_SCREEN + 1), mStartY);
        mPathMeasure.setPath(mSnowPath, false);
        mSnowPath.op(mBottomPath, Path.Op.UNION);
        mMatrix.reset();
        //方式一
        mPathMeasure.getPosTan(mPathMeasure.getLength() * mFraction, pos, tan);
        float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180f / Math.PI);
        mMatrix.postRotate(degrees, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
        mMatrix.postTranslate(pos[0] - mBitmap.getWidth() / 2, pos[1] - mBitmap.getHeight());

        //方式二
      /*
        mPathMeasure.getMatrix((int) (mPathMeasure.getLength() * mFraction), mMatrix, PathMeasure.TANGENT_MATRIX_FLAG | PathMeasure.POSITION_MATRIX_FLAG);
        mMatrix.preTranslate(mBitmap.getWidth() / 2, -mBitmap.getHeight());*/

        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        canvas.drawPath(mSnowPath, mPaint);
        
    }
}
