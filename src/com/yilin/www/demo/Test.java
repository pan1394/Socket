package com.yilin.www.demo;

import java.util.Scanner;

public class Test {

	public static void main(String[] args) {
		System.out.println("Hello World.");
		System.out.println("Please enter your name: ");
		Scanner s = new Scanner(System.in);
		String name = s.nextLine(); 
		System.out.printf("Hello, My Lord %s." , name );
	}
}
