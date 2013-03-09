package com.fonserbc.bouncegl.utils;

import android.content.Context;
import android.opengl.GLES20;

import com.fonserbc.bouncegl.GameGLRenderer;

public abstract class Shape {
	protected int mProgram;	
	
	public Shape (Context context, int vertId, int fragId) {
		int vertexShader = GameGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, Utilities.stringFromResource(context, vertId));
		int fragmentShader = GameGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, Utilities.stringFromResource(context, fragId));
		
        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
	}
	
	public abstract void draw (float[] mvpMatrix);
}
