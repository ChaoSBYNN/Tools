package com.core;

public class TestEnum {

	public enum Test {
		DIAMONDS,
		CLUBS,
		HEARTS,
		SPADES;
	}
	
	public enum Colors {

		DIAMONDS(701),
		CLUBS(702),
		HEARTS(703),
		SPADES(704);

		private int _value;

		private Colors(int value) {
			_value = value;
		}

		public int value() {
			return _value;
		}
	}
	
	public void test(){
		
		for( Colors colors : Colors.values() ){
			
			System.out.println(colors.value());
			
		}
		
	}
	
	
	public static void main(String[] args) {
		
		TestEnum te = new TestEnum();
		
		te.test();
		
	}
	
}
