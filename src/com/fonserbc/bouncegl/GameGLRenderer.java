package com.fonserbc.bouncegl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.fonserbc.bouncegl.utils.Plane;
import com.fonserbc.bouncegl.utils.Timer;
import com.fonserbc.bouncegl.utils.Utilities;
import com.fonserbc.bouncegl.utils.maths.Vector3;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class GameGLRenderer implements GLSurfaceView.Renderer {

	private static final float FRICTION = 50.0f;

	private Context mContext;
	
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mNormalMatrix = new float[16];
    
    private Plane[] planes;
    
    private int basicShader;
    
    private Timer timer;
    
    private float mXAngle = 0;
    private float mYAngle = 0;
    private float mVXAngle = 0;
    private float mVYAngle = 0;
    
    private float[] mCurrRotation = new float[16];
    
    private enum RotationMode {
    	Normal,
    	Stopping
    }
    private RotationMode mMode = RotationMode.Normal;
    
    public GameGLRenderer (Context context) {
    	mContext = context;
    	
    	timer = new Timer();
    }
	
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		
		GLES20.glClearColor(0f, 0.1f, 0.2f, 1.0f);
		
		Matrix.setLookAtM(mVMatrix, 0,  0,0,1.5f,  0,0,0,  0,1,0);
		Matrix.setIdentityM(mCurrRotation, 0);
		
		String vertexCode = Utilities.stringFromResource(mContext, R.raw.textvert2);
		String fragmentCode = Utilities.stringFromResource(mContext, R.raw.testfrag2);
		
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexCode);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode);
		
        basicShader = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(basicShader, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(basicShader, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(basicShader);
        GLES20.glUseProgram(basicShader);
        
        int mSpeedPointer = GLES20.glGetUniformLocation(basicShader, "speed");
        GameGLRenderer.checkGlError("glGetUniformLocation");
		GLES20.glUniform1fv(mSpeedPointer, 1, new float[] {0}, 0);
		
		int mTilingPointer = GLES20.glGetUniformLocation(basicShader, "tiling");
        GameGLRenderer.checkGlError("glGetUniformLocation");
		GLES20.glUniform1fv(mTilingPointer, 1, new float[] {4}, 0);
		
		int mStrokePointer = GLES20.glGetUniformLocation(basicShader, "lineSize");
        GameGLRenderer.checkGlError("glGetUniformLocation");
		GLES20.glUniform1fv(mStrokePointer, 1, new float[] {0.1f}, 0);
		
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glClearDepthf(1.0f);
		GLES20.glDepthFunc( GLES20.GL_LEQUAL );
		GLES20.glDepthMask( true );

		initScene();
	}
	
	private void initScene() {
		planes = new Plane[6];
		
		planes[0] = new Plane(basicShader, //Top
				new Vector3(-0.5f, 0.5f, -0.5f),
				new Vector3(0, 0, 1),
				new Vector3(1, 0, 0), 1);
		planes[0].setColor(new float[] {1f,1f,0f,1}); // YELLOW
		
		planes[1] = new Plane(basicShader,	//Left
				new Vector3(-0.5f, -0.5f, -0.5f),
				new Vector3(0, 1, 0),
				new Vector3(0, 0, 1), 1);
		planes[1].setColor(new float[] {1f,0f,0f,1}); // RED
		
		planes[2] = new Plane(basicShader,	//Bot
				new Vector3(-0.5f, -0.5f, 0.5f),
				new Vector3(0, 0, -1),
				new Vector3(1, 0, 0), 1);
		planes[2].setColor(new float[] {0f,1f,0f,1}); // GREEN
		
		planes[3] = new Plane(basicShader,	//Right
				new Vector3(0.5f, -0.5f, 0.5f),
				new Vector3(0, 1, 0),
				new Vector3(0, 0, -1), 1);
		planes[3].setColor(new float[] {0f,0f,1f,1}); // BLUE
		
		planes[4] = new Plane(basicShader,	//Front
				new Vector3(-0.5f, -0.5f, 0.5f),
				new Vector3(0, 1, 0),
				new Vector3(1, 0, 0), 1);
		planes[4].setColor(new float[] {1f,0f,1f,1}); // MAGENTA
		
		planes[5] = new Plane(basicShader,	//Back
				new Vector3(0.5f, -0.5f, -0.5f),
				new Vector3(0, 1, 0),
				new Vector3(-1, 0, 0), 1);
		planes[5].setColor(new float[] {0f,1f,1f,1}); // CYAN
	}

	public void onDrawFrame(GL10 unused) {
		
		float deltaTime = timer.tick();
		update(deltaTime);

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
			
		float[] aux = new float[16];
		float[] aux2 = new float[16];
		Matrix.setIdentityM(aux, 0);
		Matrix.rotateM(aux, 0, mXAngle, 1, 0, 0);
		Matrix.rotateM(aux, 0, mYAngle, 0, 1, 0);
		aux2 = mCurrRotation.clone();
		Matrix.multiplyMM(mCurrRotation, 0, aux, 0, aux2, 0);
		mXAngle = mYAngle = 0;
		Matrix.multiplyMM(aux, 0, mVMatrix, 0, mCurrRotation, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, aux, 0);
		
		Matrix.invertM(aux2, 0, aux, 0);
		Matrix.transposeM(mNormalMatrix, 0, aux2, 0);
		
		int mTimePointer = GLES20.glGetUniformLocation(basicShader, "time");
		GameGLRenderer.checkGlError("glGetUniformLocation");
		GLES20.glUniform1fv(mTimePointer, 1, new float[] {timer.getGameTime()}, 0);
		
		int mMVPMatrixHandle = GLES20.glGetUniformLocation(basicShader, "modelViewProjectionMatrix");
        GameGLRenderer.checkGlError("glGetUniformLocation");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GameGLRenderer.checkGlError("glUniformMatrix4fv");
     
        int mNormalHandle = GLES20.glGetUniformLocation(basicShader, "normalMatrix");
        GameGLRenderer.checkGlError("glGetUniformLocation");
        GLES20.glUniformMatrix4fv(mNormalHandle, 1, false, mNormalMatrix, 0);
        GameGLRenderer.checkGlError("glUniformMatrix4fv");
		
		for (int i = 0; i < planes.length; ++i) {
			planes[i].draw(mMVPMatrix);
		}
	}
	
	public void onSurfaceChanged(GL10 unused, int width, int height) {

		GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        float size = 1;
        Matrix.frustumM(mProjMatrix, 0, -ratio*size, ratio*size, -size, size, 0.5f, 7);
	}

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
    
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("BOUNCE_GL", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }	

	private void update(float deltaTime) {
		mXAngle += mVXAngle*deltaTime;
		if (mXAngle > 180f) mXAngle -= 360;
		else if (mXAngle < -180f) mXAngle += 360;
		
		mYAngle += mVYAngle*deltaTime;
		if (mYAngle > 180f) mYAngle -= 360;
		else if (mYAngle < -180f) mYAngle += 360;
		
		switch (mMode) {
			case Normal:
				if (mVXAngle > 0) {
					mVXAngle -= FRICTION*deltaTime;
					if (mVXAngle < 0) mVXAngle = 0;
				}
				else if (mVXAngle < 0) {
					mVXAngle += FRICTION*deltaTime;
					if (mVXAngle > 0) mVXAngle = 0;
				}
				
				if (mVYAngle > 0) {
					mVYAngle -= FRICTION*deltaTime;
					if (mVYAngle < 0) mVYAngle = 0;
				}
				else if (mVYAngle < 0) {
					mVYAngle += FRICTION*deltaTime;
					if (mVYAngle > 0) mVYAngle = 0;
				}
				break;
			case Stopping:
				mVXAngle -= mVXAngle*2*deltaTime;
				mVYAngle -= mVYAngle*2*deltaTime;
		}
	}
	
	public void touchMovement(float x, float y) {
		mMode = RotationMode.Normal;
		mVXAngle += x;
		mVYAngle += y;
	}

	public void backPressed() {
		mMode = RotationMode.Stopping;
	}
}
