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

	private Context mContext;
	
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mNormalMatrix = new float[16];
    
    private Plane[] planes;
    
    private int basicShader;
    
    private Timer timer;
    
    public GameGLRenderer (Context context) {
    	mContext = context;
    	
    	timer = new Timer();
    }
	
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		
		GLES20.glClearColor(0f, 0.1f, 0.2f, 1.0f);
		
		Matrix.setLookAtM(mVMatrix, 0,  0,1,2,  0,1,0,  0,1,0);
		
		String vertexCode = Utilities.stringFromResource(mContext, R.raw.testvert);
		String fragmentCode = Utilities.stringFromResource(mContext, R.raw.testfrag);
		
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexCode);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode);
		
        basicShader = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(basicShader, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(basicShader, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(basicShader);
        GLES20.glUseProgram(basicShader);
        
        int mSpeedPointer = GLES20.glGetUniformLocation(basicShader, "speed");
        GameGLRenderer.checkGlError("glGetUniformLocation");
		GLES20.glUniform1fv(mSpeedPointer, 1, new float[] {1}, 0);
		
		planes = new Plane[4];
		
		planes[0] = new Plane(basicShader,
				new Vector3(-1, 0, 0),
				new Vector3(0, 0, -2),
				new Vector3(2, 0, 0), 1);
		planes[0].setColor(new float[] {0.5f,0.6f,0.5f,1}); //GRAYGREEN
		
		planes[1] = new Plane(basicShader,	//Left
				new Vector3(-1, 0, 0),
				new Vector3(0, 2, 0),
				new Vector3(0, 0, -2), 1);
		planes[1].setColor(new float[] {1f,0f,0f,1}); // RED
		
		planes[2] = new Plane(basicShader,	//Top
				new Vector3(1, 2, 0),
				new Vector3(0, 0, -2),
				new Vector3(-2, 0, 0), 1);
		planes[2].setColor(new float[] {0f,1f,0f,1}); //GREEN
		
		planes[3] = new Plane(basicShader,	//Right
				new Vector3(1, 0, -2),
				new Vector3(0, 2, 0),
				new Vector3(0, 0, 2), 1);
		planes[3].setColor(new float[] {0f,0f,1f,1}); // BLUE
	}
	
	public void onDrawFrame(GL10 unused) {
		
		timer.tick();

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
			
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		
		float[] aux = new float[16];
		Matrix.invertM(aux, 0, mVMatrix, 0);
		Matrix.transposeM(mNormalMatrix, 0, aux, 0);
		
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
}
