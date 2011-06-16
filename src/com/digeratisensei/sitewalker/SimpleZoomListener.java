package com.digeratisensei.sitewalker;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;

import com.digeratisensei.sitewalker.classes.Reading;

public class SimpleZoomListener implements View.OnTouchListener {
	
	public enum ControlType {
        PAN, ZOOM, SELECT
    }

    private ControlType mControlType = ControlType.ZOOM;

    private ZoomState mState;

    private float mX;
    private float mY;

    public void setZoomState(ZoomState state) {
        mState = state;
    }

    public void setControlType(ControlType controlType) {
        mControlType = controlType;
    }

    private void openStrengths(Context context) {
    	Intent ssIntent = new Intent();
    	ssIntent.setClass(context, Reading.class);
    	context.startActivity(ssIntent);
    }
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        int openStrengths = 0;
        final float x = event.getX();
        final float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mX = x;
                mY = y;
                break;

            case MotionEvent.ACTION_MOVE: {
                final float dx = (x - mX) / v.getWidth();
                final float dy = (y - mY) / v.getHeight();

                if (mControlType == ControlType.ZOOM) {
                    mState.setZoom(mState.getZoom() * (float)Math.pow(20, -dy));
                    mState.notifyObservers();
                } else {
                    mState.setPanX(mState.getPanX() - dx);
                    mState.setPanY(mState.getPanY() - dy);
                    mState.notifyObservers();
                }

                mX = x;
                mY = y;
                break;
            }
            case MotionEvent.ACTION_UP:
            	if (mControlType == ControlType.SELECT) {
            		mX = x;
            		mY = y;
            		openStrengths = 1;
            	}
            }
        if(mControlType == ControlType.SELECT && openStrengths == 1) {
        	openStrengths(v.getContext());
        }

        return true;
    }

}
