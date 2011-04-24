package com.snapperfiche.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utility {
	public static String ConvertStreamToString(InputStream stream) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		StringBuilder builder = new StringBuilder();
		for(String line = null; (line = reader.readLine()) != null;){
			builder.append(line).append("\n");
		}
		return builder.toString();
	}
}
