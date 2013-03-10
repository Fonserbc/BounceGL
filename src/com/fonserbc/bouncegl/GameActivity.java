package com.fonserbc.bouncegl;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

	private GLSurfaceView mGLView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        mGLView = new GameGLSurfaceView(this);
        setContentView(mGLView);
    }
    
    
}

class GameGLSurfaceView extends GLSurfaceView {

	private final GameGLRenderer mRenderer;
	
    public GameGLSurfaceView (Context context) {
		super(context);

		setEGLContextClientVersion(2);
		
		mRenderer = new GameGLRenderer(context);
		setRenderer(mRenderer);
		
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
    
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                mRenderer.touchMovement(dy * TOUCH_SCALE_FACTOR, dx * TOUCH_SCALE_FACTOR);
                requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}
