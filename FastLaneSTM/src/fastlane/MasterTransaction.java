package fastlane;

public abstract class MasterTransaction implements Transaction {
	
	private Framework f;
	
	public MasterTransaction (Framework fw) {
		f = fw;
	}
	
	public int read(int i) {
		System.out.println("master read");
		return f.getData(i);
	}
	
	public void write(int i, int n) {
		System.out.println("master writes");
		if (f.getCounter() % 2 == 0) {
			f.incrementCounter();
		}
		f.setDirty(i, f.getCounter());
		f.setData(i, n);
	}
	
	public void commit() {
		if (f.getCounter() % 2 == 1) {
			f.incrementCounter();
		}
		f.unlockMaster();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
	}
	
	public void run() {
		doTransaction();
	}
}
