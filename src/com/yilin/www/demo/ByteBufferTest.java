package com.yilin.www.demo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.stream.Stream;

public class ByteBufferTest {

	final static int BUFFER_SIZE = 10;
	static int count =0;
	public static void main(String[] args) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		String testdata = "public class Task2 {  \r\n" + 
				"    public static void main(String[] args) {  \r\n" + 
				"        TimerTask task = new TimerTask() {  \r\n" + 
				"            @Override  \r\n" + 
				"            public void run() {  \r\n" + 
				"                // task to run goes here  \r\n" + 
				"                System.out.println(\"Hello !!!\");  \r\n" + 
				"            }  \r\n" + 
				"        };  \r\n" + 
				"        Timer timer = new Timer();  \r\n" + 
				"        long delay = 0;  \r\n" + 
				"        long intevalPeriod = 1 * 1000;  \r\n" + 
				"        // schedules the task to be run in an interval  \r\n" + 
				"        timer.scheduleAtFixedRate(task, delay, intevalPeriod);  \r\n" + 
				"    } // end of main  \r\n" + 
				"}  ";
		fillByteBuffer(buffer, testdata);
		fillByteArrayOutputStream(new ByteArrayOutputStream(), testdata);
	}
	private static void fillByteBuffer(ByteBuffer buffer, String testdata) {
		byte[] all = testdata.getBytes();
		int length = all.length;
		int mod = length%BUFFER_SIZE;
		int count = length/BUFFER_SIZE + (mod>0?1:0);
		for(int i=0; i<count; i++) {
			if(i==count-1) {
				buffer.put(all,i*BUFFER_SIZE,   mod);
			}else {
				buffer.put(all, i*BUFFER_SIZE,  BUFFER_SIZE);
			}
			
			buffer.flip();
			ReadByteBuffer(buffer);
			buffer.clear();
		}
	}
	private static void ReadByteBuffer(ByteBuffer buffer) {
		byte[] arr = new byte[BUFFER_SIZE];
		while(buffer.hasRemaining()) {
			int length = Math.min(buffer.remaining(),BUFFER_SIZE);
			buffer.get(arr, 0, length);
			System.out.print(new String(arr));
		}  
	}
	
	 private static void fillByteArrayOutputStream(OutputStream out, String testdata) throws IOException {
		 byte[] all = testdata.getBytes();
		 out.write(all, 0, all.length);
		 InputStream in = new ByteArrayInputStream(all);
		 ReadByInputStream(in);
		 //ReaderByReader(in);
		 
	 }
	 
	 private static void ReadByInputStream(InputStream in) throws IOException { 
		 int length = in.available();
		 byte[] res = new byte[length];
		 in.read(res); 
		 System.out.println(new String(res));
 
	 }
	 private static void ReadByReader(InputStream in) throws IOException {
		 BufferedReader br = new BufferedReader(new InputStreamReader(in));String line = null;
		 while((line=br.readLine()) != null)
			 System.out.println(line);
		/* Scanner reader = new Scanner(new InputStreamReader(in)); 
		 int length = in.available();
		 byte[] res = new byte[length];
		 in.read(res);
		 while(reader.hasNext())
			 System.out.println(reader.nextLine()); */
		 
	 }
}
