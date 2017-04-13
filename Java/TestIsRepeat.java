package com.chaos_zhang.main;

import java.util.ArrayList;
import java.util.HashMap;

public class TestIsRepeat {

	public static void main(String[] args) {
		
		ArrayList<Byte> cards = new ArrayList<Byte>();
		
//		cards.add((byte) 0x10);
		cards.add((byte) 0x10);
		cards.add((byte) 0x11);
		cards.add((byte) 0x11);
		cards.add((byte) 0x12);
		cards.add((byte) 0x12);
		cards.add((byte) 0x12);

	    HashMap<String,String> map = new HashMap<String, String>();
	    
	    for(Byte checkCard : cards) {

	    	System.out.println("byte value " + checkCard);

	    	
	        if(map.containsKey(checkCard.toString())){
	            map.put(checkCard.toString(),"true");
	        }else{
	            map.put(checkCard.toString(),"false");
	        }

	    }

	    if(map.containsValue("false")) {
	        System.out.println(false);
	    }else{
	        System.out.println(true);
	    }
		
	}
	
	
}
