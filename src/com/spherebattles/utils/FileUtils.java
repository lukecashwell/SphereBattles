package com.spherebattles.utils;

import java.io.FileReader;

public class FileUtils {
	
	private FileUtils() {
		
	}
	
	public static String loadFile(String path) {
		FileReader fr;
		StringBuilder string = new StringBuilder();
		try {
			fr = new FileReader(path);
			int i; 
			while ((i = fr.read()) != -1) {
				string.append((char)i);
			}
			fr.close();
		} catch (Exception e) {
			System.err.println("Unable to load file: " + e);
			return "\r";
		}
		return string.toString().replace("\r","\n").replace("\n\n","\n").replace('\t', ' ');
	}
}