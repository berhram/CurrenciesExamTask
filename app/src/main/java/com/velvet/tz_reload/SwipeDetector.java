package com.velvet.tz_reload;

import android.util.Log;
import android.view.MotionEvent;

public abstract class SwipeDetector {

    public static final String TAG = "TZ log";

    private boolean isStarted = false;
    private float startX = 0;
    private float startY = 0;
    private int minTouchLen = 10;

    public SwipeDetector(int minTouchLen) {
        this.minTouchLen = minTouchLen * 5;
    }

    public abstract void onSwipeDetected(Direction direction);

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX=event.getX();
                startY=event.getY();
                isStarted = true;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float dx = event.getX() - startX;
                float dy = event.getY() - startY;
                if (calcDist(dx,dy)>=minTouchLen) {
                    onSwipeDetected(getDirection(dx, dy));
                }
                startY = startX = 0;
                isStarted = false;
                break;
            default:
                startY = startX = 0;
                isStarted = false;
                Log.d(TAG, "Touch Error");
        }
        return false;
    }

    private Direction getDirection(float dx, float dy) {
        return Direction.get(calcAngle(dx, dy));
    }

    private int calcAngle(float dx, float dy) {
        return (int) ((Math.atan2(dy, dx) + Math.PI) * 180 / Math.PI + 180)%360;
    }

    private double calcDist(float dx, float dy) {
        return Math.sqrt(dx*dx + dy*dy);
    }

    public enum Direction {
        UN_EXPT,
        LEFT,
        RIGHT,
        DOWN,
        UP;

        public static Direction get (int angle) {
            Direction res = UN_EXPT;
            if (angle>=35 && angle<=135) {
                res = UP;
            }
            else if (angle>=135 && angle<=225) {
                res=RIGHT;
            }
            else if (angle>=225 && angle<=315) {
                res=DOWN;
            }
            else if (angle>=315 && angle<=360) {
                res=LEFT;
            }
            return res;
        }
    }
}
