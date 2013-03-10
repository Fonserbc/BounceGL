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
    private int mTexCoordHandle;
    private int mColorHandle;
    private int mNormalHandle;
	private final FloatBuffer vertexBuffer;
	private final FloatBuffer texCoordBuffer;
    private final ShortBuffer drawListBuffer;
    
    private float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    private float normal[];
    private final float textureCoordData[] = { 0, 1,  0, 0,  1, 0,  1, 1};
    
	
	public Plane(int programId, Vector3 from, Vector3 up, Vector3 right, int divisions) {
		mProgram = programId;
		
		normal = new float[3];
		normal = right.cross(up).normalize().array();
		
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
        
        ByteBuffer tcb = ByteBuffer.allocateDirect(textureCoordData.length * 4);
        tcb.order(ByteOrder.nativeOrder());
        texCoordBuffer = tcb.asFloatBuffer();
        texCoordBuffer.put(textureCoordData);
        texCoordBuffer.position(0);
	}
	
	@Override
	public void draw(float[] mvpMatrix) {
		
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vertex");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
		
		mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "texCoord");
		GLES20.glEnableVertexAttribArray(mTexCoordHandle);
		GLES20.glVertexAttribPointer(mTexCoordHandle, 2,
                GLES20.GL_FLOAT, false,
                4*2, texCoordBuffer);
		
		mNormalHandle = GLES20.glGetUniformLocation(mProgram, "normal");
		GameGLRenderer.checkGlError("glGetUniformLocation");
		GLES20.glUniform3fv(mNormalHandle, 1, normal, 0);
		
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "color");
		GameGLRenderer.checkGlError("glGetUniformLocation");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);        
        
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
