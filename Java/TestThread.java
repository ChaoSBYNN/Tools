class MyThead implements Runnable {
	public void run() {
		while(true){
			
			System.out.println("我休息了！");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
			System.out.println("一秒后在叫我吧！");
		}
	}
}

public class TestThread {
	public static void main(String[] args) {
		MyThead myThead = new MyThead();
		Thread thread = new Thread(myThead);
		thread.start();
	}
}