class MyThead implements Runnable {
	public void run() {
		while(true){
			
			System.out.println("����Ϣ�ˣ�");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
			System.out.println("һ����ڽ��Ұɣ�");
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