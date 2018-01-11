package com.yilin.www.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.Set;

public class ClientNIO {

	public static void main(String[] args) throws IOException{
		ClientNIO.init();
	}
	public  static void  init() throws IOException{
		final Selector selector = Selector.open();
		SocketChannel ssc = SocketChannel.open();
		ssc.configureBlocking(false); 
		SocketAddress endpoint = new InetSocketAddress("127.0.0.1", 2000);
		ssc.connect(endpoint); 
	    ssc.register(selector, SelectionKey.OP_CONNECT);
		Scanner scan = new Scanner(System.in);
		String line =""; 
		new Thread( new Runnable(){

			@Override
			public void run() {
				try {
					while(selector.select()>0){ 
						
						Set<SelectionKey> selectedKeys = selector.selectedKeys();
						for(SelectionKey key : selectedKeys){
							selector.selectedKeys().remove(key); 
							if(key.isConnectable()){
								SocketChannel sc =(SocketChannel) key.channel();
								if(sc.isConnectionPending()){
									sc.finishConnect();
								}
								read(sc);
								sc.configureBlocking(false);
								sc.register(selector, SelectionKey.OP_READ);  
								String name= "中国人";
								//ByteBuffer b = (Charset.forName("UTF-8").encode("Hello, Server, 你好中国人")); 
								sc.write(ByteBuffer.wrap(name.getBytes()));
							}
							if(key.isReadable()){
								SocketChannel sc = (SocketChannel) key.channel();
								read(sc);
							} 
						 }
					   }
				} catch (ClosedChannelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}).start();
		while(scan.hasNextLine()){
			line = scan.nextLine();
			System.out.println("echo:" + line);
			ssc.write(Charset.forName("UTF-8").encode(line));
		}
		
	}
	
	private static void read(SocketChannel ch) throws IOException{
		ByteBuffer b = ByteBuffer.allocate(1024);
		CharBuffer c = b.asCharBuffer();
		int i =0;
		StringBuilder s = new StringBuilder();
		while((i = ch.read(b))> 0) {
			b.flip();
			s.append(Charset.forName("UTF-8").decode(b));
		}
		System.out.println(s.toString());
	}
}
