package com.beck.matthew.androidcartesiangraphview;

// This class creates a cartesian graph that can be moved around and zoomed in and out.
// Created by Matthew Beck

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;

import java.util.Vector;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class CartesianGraphView extends View {

    protected static int MAX_GRID_LINES = 16;
    int MIN_GRID_LINES = 7;

    private GestureDetectorCompat detectorCompat;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.0f;

    Paint bigLine;
    Paint smallLine;
    Paint textStyle;
    private float scale;
    private float unitsScale;
    private boolean maxScale;
    private boolean minScale;
    float textPadding;
    String[] YStrings;
    String[] XStrings;
    Vector<PathEquation> pathEquations;// this vector will hold the equations that will be drawn. This is optional and can be removed easily

    public CartesianGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        detectorCompat = new GestureDetectorCompat(context, new GestureListener());
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mScroller = new OverScroller(context);

        init();
    }
    private void init(){// setting up everything
        bigLine = new Paint();
        bigLine.setColor(Color.BLACK);
        bigLine.setStrokeWidth(5);
        bigLine.setStyle(Paint.Style.STROKE);

        smallLine = new Paint();
        smallLine.setColor(Color.GRAY);
        smallLine.setStrokeWidth(3);
        smallLine.setStyle(Paint.Style.STROKE);

        textStyle = new Paint();
        textStyle.setColor(Color.BLACK);
        textStyle.setStyle(Paint.Style.FILL);
        textStyle.setTextSize(45);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        scale = displayMetrics.heightPixels/10.0f;// this sets the vertical lines to be drawn to 10.
        // To change this you'll have also adjust the max and min grid lines

        unitsScale = 1;// this is for the text
        textPadding = 5/mScaleFactor;
        maxScale = false;
        minScale = false;

        YStrings = new String[MAX_GRID_LINES];
        XStrings = new String[MAX_GRID_LINES];

        pathEquations = new Vector<>(5,5);
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        float width = getWidth() / mScaleFactor;
        float height = getHeight() / mScaleFactor;

        float centerX = width/2.0f;
        float centerY = height/2.0f;

        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.translate(newX,newY);

        int gridsAwayY = Math.round((centerY+newY) / scale);// calculating the grids away from the origin on the graph going up
        int gridsHiddenY= Math.round(((centerY+newY)-height) / scale) + Math.round(centerY/scale) - 1;// calculates the grid lines hidden from the origin going up
        int horizontalLinesToDraw = ((gridsAwayY-gridsHiddenY)*2)+1;// due to a variance of need lines when zooming we need to calculate horizontal lines to draw
        float calculateY;

        // same calculations as y except going from the origin to the right.
        int gridsAwayX = Math.round((centerX-newX) / scale);
        int gridsHiddenX= Math.round(((centerX+newX)-width) / scale) + Math.round(centerX/scale) + 1;
        gridsHiddenX = gridsHiddenX*-1;// must be inverted
        int verticalLinesToDraw = ((gridsAwayX-gridsHiddenX)*2)+1;
        float calculateX;

         if (horizontalLinesToDraw >= MAX_GRID_LINES || verticalLinesToDraw >= MAX_GRID_LINES){
             scale *= 2;// the scale goes by twos
             // When zooming it will be powers of two. To change this you must change:
             // The scale/unitsScale multiplication value and max and min grid lines
             unitsScale *= 2;

             gridsAwayY = Math.round((centerY+newY) / scale);
             gridsHiddenY= Math.round(((centerY+newY)-height) / scale) + Math.round(centerY/scale) - 1;
             horizontalLinesToDraw = ((gridsAwayY-gridsHiddenY)*2)+1;

             gridsAwayX = Math.round((centerX-newX) / scale);
             gridsHiddenX= Math.round(((centerX+newX)-width) / scale) + Math.round(centerX/scale) + 1;
             gridsHiddenX = gridsHiddenX*-1;
             verticalLinesToDraw = ((gridsAwayX-gridsHiddenX)*2)+1;
         } else if (horizontalLinesToDraw <= MIN_GRID_LINES && verticalLinesToDraw <= MIN_GRID_LINES){
             scale /= 2;
             unitsScale /= 2;

             gridsAwayY = Math.round((centerY+newY) / scale);
             gridsHiddenY= Math.round(((centerY+newY)-height) / scale) + Math.round(centerY/scale) - 1;
             horizontalLinesToDraw = ((gridsAwayY-gridsHiddenY)*2)+1;

             gridsAwayX = Math.round((centerX-newX) / scale);
             gridsHiddenX= Math.round(((centerX+newX)-width) / scale) + Math.round(centerX/scale) + 1;
             gridsHiddenX = gridsHiddenX*-1;
             verticalLinesToDraw = ((gridsAwayX-gridsHiddenX)*2)+1;
         }

        maxScale = unitsScale > 10000000.0f;// max value that can be zoomed to
        minScale = unitsScale < 0.00001f;// min value that can be zoomed in

        for (int i = 0; i < MAX_GRID_LINES; i++){// adding the Y line text
            YStrings[i] = ((gridsAwayY - i) * unitsScale) + "";
        }
        for (int i = 0; i < MAX_GRID_LINES; i++){// adding the X line text
            XStrings[i] = ((gridsAwayX - i) * unitsScale) + "";
        }

        for (int i = 0; i < horizontalLinesToDraw; i++){

            calculateY = centerY - (scale*(gridsAwayY-i));// calculating the y position of the line
            canvas.drawLine(-newX, calculateY, width-newX, calculateY, smallLine);

            if (centerX < Math.abs(newX)){// this if block handles the text going off the center line
                if (newX > 0){// this draws text if it is all negative x values
                    canvas.drawText(YStrings[i], (width-newX) - (textPadding + textStyle.measureText(YStrings[i])),
                                  calculateY - textPadding, textStyle);
                }else {// draws text when all x values are positive
                    canvas.drawText(YStrings[i], -newX + textPadding, calculateY - textPadding, textStyle);
                }
            }else {// draws text if x origin can be seen
                canvas.drawText(YStrings[i], centerX + textPadding, calculateY - textPadding, textStyle);
            }
        }

        for (int i = 0; i < verticalLinesToDraw; i++){

            calculateX = centerX + (scale*(gridsAwayX-i)); // calculating the x position of the line
            canvas.drawLine(calculateX, -newY, calculateX, height-newY, smallLine);

            if (centerY < Math.abs(newY)){
                if (newY > 0){// draws text if all y values are positive
                    canvas.drawText(XStrings[i], calculateX + textPadding, height-newY -textPadding, textStyle);
                }else{// draws text if all y values are negative
                    canvas.drawText(XStrings[i], calculateX + textPadding, -newY + textPadding + textStyle.getTextSize(), textStyle);
                }
            }else{// draws text if y origin is visible
                canvas.drawText(XStrings[i], calculateX + textPadding, centerY - textPadding, textStyle);
            }
        }

        canvas.drawLine(centerX, -newY, centerX, height-newY, bigLine);// vertical big line
        canvas.drawLine(-newX, centerY, width-newX, centerY, bigLine);// horizontal big line

        if (!pathEquations.isEmpty()) {// this draws the equations if there are any
            for (PathEquation item: pathEquations) {
                item.drawEquations(gridsAwayX - verticalLinesToDraw,
                        gridsAwayX + 1, unitsScale, scale, centerX, centerY, canvas, bigLine);
            }
        }
    }

    public Vector<PathEquation> getPathEquations(){
        return pathEquations;
    }

    // scaleFactorChange changes stuff only when the scale changes
    private void scaleFactorChange(){
        textStyle.setTextSize(50/mScaleFactor);
        bigLine.setStrokeWidth(5/mScaleFactor);
        smallLine.setStrokeWidth(3/mScaleFactor);

        textPadding = 5/mScaleFactor;
    }

    private float mPreviousX;
    private float mPreviousY;
    private float newX, newY = 0;

    private int mActivePointerId = INVALID_POINTER_ID;
    private OverScroller mScroller;

    @SuppressWarnings("deprecation")
    public boolean onTouchEvent(MotionEvent e) {
        this.detectorCompat.onTouchEvent(e);
        this.mScaleDetector.onTouchEvent(e);
        final int action = MotionEventCompat.getActionMasked(e);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(e);
                final float x = MotionEventCompat.getX(e, pointerIndex);
                final float y = MotionEventCompat.getY(e, pointerIndex);

                // Remember where we started (for dragging)
                mPreviousX = x;
                mPreviousY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(e, 0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(e, mActivePointerId);

                final float x = MotionEventCompat.getX(e, pointerIndex);
                final float y = MotionEventCompat.getY(e, pointerIndex);

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {
                    // Calculate the distance moved
                    final float dx = x - mPreviousX;
                    final float dy = y - mPreviousY;

                    newX += (dx/mScaleFactor);
                    newY += (dy/mScaleFactor);

                    invalidate();
                }
                // Remember this touch position for the next move event
                mPreviousX = x;
                mPreviousY = y;

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = MotionEventCompat.getActionIndex(e);
                final int pointerId = MotionEventCompat.getPointerId(e, pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mPreviousX = MotionEventCompat.getX(e, newPointerIndex);
                    mPreviousY = MotionEventCompat.getY(e, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(e, newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            // Abort any active scroll animations and invalidate.
            mScroller.forceFinished(true);
            // There is also a compatibility version:
            // ViewCompat.postInvalidateOnAnimation
            postInvalidateOnAnimation();
            return true;
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // You don't use a scroller in onScroll because you don't need to animate
            // a scroll. The scroll occurs instantly in response to touch feedback.
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Before flinging, abort the current animation.
            mScroller.forceFinished(true);
            // Begin the scroll animation
            mScroller.fling(
                    // Current scroll position
                    (int) mPreviousX,
                    (int) mPreviousY,
                    // Velocities, negated for natural touch response
                    (int) velocityX/5,
                    (int) velocityY/5,
                    // Minimum and maximum scroll positions. The minimum scroll
                    // position is generally zero and the maximum scroll position
                    // is generally the content size less the screen size. So if the
                    // content width is 1000 pixels and the screen width is 200
                    // pixels, the maximum scroll offset should be 800 pixels.
                    -getWidth()*4, getWidth()*4,
                    -getHeight()*4, getHeight()*4
                    // The maximum overscroll bounds. This is useful when using
                    // the EdgeEffect class to draw overscroll "glow" overlays.
                    );
            //mScroller.startScroll((int)mPreviousX, (int)mPreviousY, 0,(int) velocityY);
            // Invalidate to trigger computeScroll()
            postInvalidateOnAnimation();
            return true;
        }
    }
    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if ((!maxScale || detector.getScaleFactor() > 1.0f) && (!minScale || detector.getScaleFactor() < 1.0f)) {// making sure the max scale and min scale are not hit
                mScaleFactor *= detector.getScaleFactor();

                // Don't let the object get too small or too large.
                mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

                if (mScaleFactor == 0.1f) {
                    // the reason it's 0.2f and 2.5f in the next if is because if you
                    // double the min/max it will not conflict with the grid lines.
                    // For 0.2f it can actually be 0.4f and you cannot see a any skipping
                    mScaleFactor = 0.2f;
                    unitsScale *= 2;

                    newX -= newX * 0.5f; // adjusts the x and y coordinates
                    newY -= newY * 0.5f;
                } else if (mScaleFactor == 5.0f) {
                    mScaleFactor = 2.5f;
                    unitsScale /= 2;

                    newX += newX;// adjusts the x and y coordinates
                    newY += newY;
                }

                scaleFactorChange();

                invalidate();
                return true;
            } return false;
        }
    }

    public void computeScroll() {
        super.computeScroll();

        // Compute the current scroll offsets. If this returns true, then the
        // scroll has not yet finished.
        if (mScroller.computeScrollOffset()) {
            int currX = mScroller.getCurrX();
            int currY = mScroller.getCurrY();

            // Actually render the scrolled viewport

            final float dx = currX - mPreviousX;
            final float dy = currY - mPreviousY;

            newX += (dx/mScaleFactor);
            newY += (dy/mScaleFactor);

            mPreviousX = currX;
            mPreviousY = currY;


            invalidate();

        } else {
            // The scroll has finished.
        }
    }
}
