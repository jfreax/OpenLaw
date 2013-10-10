package de.jdsoft.law;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import com.nineoldandroids.animation.ValueAnimator;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private final Scroller mScroller;
    private final ValueAnimator mScrollAnimator;

    public OnSwipeTouchListener(Context context, Scroller mScroller) {
        this.mScroller = mScroller;
        this.gestureDetector =  new GestureDetector(context, new GestureListener());

        mScrollAnimator = ValueAnimator.ofFloat(0, 1);
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                tickScrollAnimation();
            }
        });
    }

    private void tickScrollAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.computeScrollOffset();
            Log.e("OpenLaw", "Set Y: " + mScroller.getCurrY());
        } else {
            //onScrollFinished();
        }
    }


    public boolean onTouch(final View v, final MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            Log.e("OpenLaw", "doerdrd");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            Log.e("OpenLaw", "uhuuuu");
            mScroller.fling(0, 10, (int)(velocityX / 3), (int)(velocityY / 3), 0, 800, 0, 6575);

            mScrollAnimator.setDuration(mScroller.getDuration());
            mScrollAnimator.start();

//            if (!mScroller.isFinished()) {
//                mScroller.computeScrollOffset();
//                Log.e("OpenLaw", "Y: " + mScroller.getCurrY());
//                Log.e("OpenLaw", "VeloY: " + velocityY);
//                Log.e("OpenLaw", "X: " + mScroller.getCurrX());
//                Log.e("OpenLaw", "VeloX: " + velocityX);
//            }
            return true;

//            boolean result = false;
//            try {
//                float diffY = e2.getY() - e1.getY();
//                float diffX = e2.getX() - e1.getX();
//                if (Math.abs(diffX) > Math.abs(diffY)) {
//                    if (Math.abs(diffX) > SWIPE_THRESHOLD
//                            && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
//                        if (diffX > 0) {
//                            result = onSwipeRight();
//                        } else {
//                            result = onSwipeLeft();
//                        }
//                    }
//                } else {
//                    if (Math.abs(diffY) > SWIPE_THRESHOLD
//                            && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
//                        if (diffY > 0) {
//                            result = onSwipeBottom();
//                        } else {
//                            result = onSwipeTop();
//                        }
//                    }
//                }
//            } catch (Exception exception) {
//                exception.printStackTrace();
//            }
//            return result;
        }
    }

    public boolean onSwipeRight() {
        return false;
    }

    public boolean onSwipeLeft() {
        return false;
    }

    public boolean onSwipeTop() {
        return false;
    }

    public boolean onSwipeBottom() {
        return false;
    }
}