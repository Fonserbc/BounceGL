package com.fonserbc.bouncegl.utils;

public abstract class Shape {
	
	protected int mProgram;
	
	public abstract void draw (float[] mvpMatrix);
}
