package com.core;

import java.util.ArrayList;

public class TestClass {

	ArrayList<Integer> lists = new ArrayList<>();
	
	public TestClass(){
		
		
	}
	
	public static void main(String[] args) {
		
		TestClass tc = new TestClass();
		
		ArrayList<Integer> l = tc.lists;
		
		l.add(1);
		l.add(2);
		l.add(3);
		
		System.out.println(l.size());
		System.out.println(tc.lists.size());
		
	}
	
}
