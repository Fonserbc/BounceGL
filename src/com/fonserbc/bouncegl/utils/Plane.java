package com.fonserbc.bouncegl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.fonserbc.bouncegl.GameGLRenderer;
import com.fonserbc.bouncegl.utils.maths.Vector3;

import android.opengl.GLES20;
import android.util.Log;

public class Plane extends Shape {
	
	private final int COORDS_PER_VERTEX = 3;	
	private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 };
	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex


    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
	private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    
    private float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    
	
	public Plane(int programId, Vector3 from, Vector3 up, Vector3 right, int divisions) {
		mProgram = programId;
		
		ByteBuffer bb = ByteBuffer.allocateDirect(3 * 4 * 4 * divisions * divisions);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		
		Vector3 botleft = from;
		Vector3 topLeft = from.add(up);
		Vector3 botright = from.add(right);
		Vector3 topright = botright.add(up);
		
		vertexBuffer.put(topLeft.array());
		vertexBuffer.put(botleft.array());
		vertexBuffer.put(botright.array());
		vertexBuffer.put(topright.array());
		vertexBuffer.position(0);
			
		ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
	}
	
	@Override
	public void draw(float[] mvpMatrix) {
		
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vertex");		
		
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
		
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "color");
		GameGLRenderer.checkGlError("glGetUniformLocation");
		
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "modelViewProjectionMatrix");
        GameGLRenderer.checkGlError("glGetUniformLocation");
        
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GameGLRenderer.checkGlError("glUniformMatrix4fv");
        
        
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        
        GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
	
	public void setColor(float[] newColor) {
		if (newColor.length != 4)
			throw new RuntimeException("Must create color with 4 element array");
		
		color[0] = newColor[0];
		color[1] = newColor[1];
		color[2] = newColor[2];
		color[3] = newColor[3];
	}
}
