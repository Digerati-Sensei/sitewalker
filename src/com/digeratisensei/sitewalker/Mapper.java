package com.digeratisensei.sitewalker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.digeratisensei.sitewalker.SimpleZoomListener.ControlType;

public class Mapper extends Activity {
	private static final int MENU_ID_ZOOM = 0;
    private static final int MENU_ID_PAN = 1;
    private static final int MENU_ID_SELECT = 2;
    private static final int MENU_ID_RESET = 3;

    private ImageZoomView mZoomView;
    private ZoomState mZoomState;
    private SimpleZoomListener mZoomListener;
    
	// These matrices will be used to move and zoom image
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapper);
        Bitmap floorplan = BitmapFactory.decodeResource(getResources(), R.drawable.floorplan);
        
        mZoomState = new ZoomState();
        mZoomListener = new SimpleZoomListener();
        mZoomListener.setZoomState(mZoomState);
        
        mZoomView = (ImageZoomView)findViewById(R.id.zoomview);
        mZoomView.setZoomState(mZoomState);
        mZoomView.setImage(floorplan);
        mZoomView.setOnTouchListener(mZoomListener);

        resetZoomState();
	}

	@Override
    protected void onDestroy() {
        super.onDestroy();

        //floorplan.recycle();
        mZoomView.setOnTouchListener(null);
        mZoomState.deleteObservers();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ID_ZOOM, 0, R.string.menu_zoom);
        menu.add(Menu.NONE, MENU_ID_PAN, 1, R.string.menu_pan);
        menu.add(Menu.NONE, MENU_ID_SELECT, 2, R.string.menu_select);
        menu.add(Menu.NONE, MENU_ID_RESET, 3, R.string.menu_reset);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_ZOOM:
                mZoomListener.setControlType(ControlType.ZOOM);
                break;

            case MENU_ID_PAN:
                mZoomListener.setControlType(ControlType.PAN);
                break;
                
            case MENU_ID_SELECT:
            	mZoomListener.setControlType(ControlType.SELECT);
            	break;
            	
            case MENU_ID_RESET:
                resetZoomState();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void resetZoomState() {
        mZoomState.setPanX(0.5f);
        mZoomState.setPanY(0.5f);
        mZoomState.setZoom(1f);
        mZoomState.notifyObservers();
    }
}
