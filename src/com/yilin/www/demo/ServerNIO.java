package com.yilin.www.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

public class ServerNIO {

	public static void main(String[] args) throws IOException{
		ServerNIO.init();
	}
	public  static void  init() throws IOException{
		Selector selector = Selector.open();
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		
		ServerSocket ss = ssc.socket();
		SocketAddress endpoint = new InetSocketAddress("127.0.0.1", 2000);
		ss.bind(endpoint);
		
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		
		while(true){ 
		selector.select();
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		for(SelectionKey key : selectedKeys){
			selector.selectedKeys().remove(key); 
			if(key.isAcceptable()){
				SocketChannel sc = ssc.accept();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ); 
				ByteBuffer b = ByteBuffer.wrap("Connected Successfully, Welcome!".getBytes()); 
				sc.write(b);
			}
			if(key.isReadable()){
				SocketChannel sc = (SocketChannel) key.channel();
				ByteBuffer b = ByteBuffer.allocate(1024);
				CharBuffer c = b.asCharBuffer();
				int i =0;
				StringBuilder s = new StringBuilder();
				while((i = sc.read(b))> 0) {
					b.flip();//中
					s.append(Charset.forName("UTF-8").decode(b));
				}
				System.out.println(s.toString());
			} 
		}
	  }
	}
}
