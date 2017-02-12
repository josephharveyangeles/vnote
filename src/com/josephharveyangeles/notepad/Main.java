package com.josephharveyangeles.notepad;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
	
	public static void main(String[] args) throws IOException, InterruptedException{
		new NotepadUI("VNote");
		//createHidden();
		//displayFiles();
	}
	
	private static void createHidden() throws IOException, InterruptedException{
		File f = File.createTempFile("myHiddenFile", ".bak");
		
		FileWriter fw = new FileWriter(f);
		fw.write("myHiddenfile");
		fw.close();
		
		String command[] = {"attrib","+h",f.getPath()};
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		
		for(File ff: f.getParentFile().listFiles()){
			System.out.println(ff.getAbsolutePath());
		}
		
		f.delete();
	}
	
	private static void displayFiles(){
		File f = new File(".");
		for(File ff: f.listFiles()){
			System.out.println(ff.getAbsolutePath());
		}
	}

}
