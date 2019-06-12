package fastlane;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Framework {
	private int cntr = 0;
	private int[] dirty;
	private ReentrantLock master;
	private ReentrantLock helpers;
	private int masterID = 0;
	
	public Framework(int memSize) {
		dirty = new int[memSize];
		master = new ReentrantLock();
		helpers = new ReentrantLock();
	}
	
	public int getCounter() {
		return cntr;
	}
	
	public void setCounter(int i) {
		cntr = i;
	}
	
	

}
