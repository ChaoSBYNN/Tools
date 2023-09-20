package com.core;

public class TestEnum2 {

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
		
		TestEnum2 te = new TestEnum2();
		
		te.test();
		
	}
	
}
