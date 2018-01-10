package com.yilin.www.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCopyUtil {

	public static void copy(File sourceFile, File copy) throws IOException {
		FileInputStream is = new FileInputStream(sourceFile);
		FileOutputStream os = new FileOutputStream(copy);
		is.getChannel().transferTo(0, is.available(), os.getChannel()); 
		is.close();
		os.close(); 
		System.out.printf("Copy the source file %s to path %s as name %s", sourceFile.getName(), copy.getAbsoluteFile().getParent(), copy.getName());
	}
	
	public static void main(String[] args) {
		/*String a = "D:\\Java\\eclipse\\eclipse.ini";
		String b = "D:\\Java\\eclipse\\eclipse.ini.copy";
		String args[0] = "C:\\Users\\Administrator\\Desktop\\操作系统\\自考操作系统简答02326.doc";
		String args[1] = "C:\\Users\\Administrator\\Desktop\\bb\\a.doc";  */
	    String arg1 = args[0];
		String arg2 = args[1]; 
		try {
			File source = new File(arg1); 
			File dest = new File(arg2); 
			FileCopyUtil.copy(source, dest);
		} catch (IOException e) { 
			e.printStackTrace();
		} 
	}
}
