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
		
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
    
    @Override
    public boolean onTouchEvent(MotionEvent e) {
    	//TODO
    	
		return super.onTouchEvent(e);
    }
}
