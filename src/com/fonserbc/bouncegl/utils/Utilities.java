package com.fonserbc.bouncegl.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class Utilities {
	public static String stringFromResource (Context context, int id) {
		InputStream inputStream = context.getResources().openRawResource(id);
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    
    	int i;
    	try {
    		i = inputStream.read();
    		while (i != -1) {
    			byteArrayOutputStream.write(i);
    			i = inputStream.read();
    		}
    		inputStream.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
 
    	return byteArrayOutputStream.toString();
	}
}
