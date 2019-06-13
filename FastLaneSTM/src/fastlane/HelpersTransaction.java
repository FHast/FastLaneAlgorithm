package fastlane;

import java.util.HashMap;
import java.util.HashSet;

public abstract class HelpersTransaction implements Transaction {

	private Framework f;
	protected int start = 0;
	
	private HashSet<Integer> readSet;
	private HashMap<Integer,Integer> writeSet;
	
	private boolean aborted = false;
	
	public HelpersTransaction(Framework fw) {
		f = fw;
		readSet = new HashSet<>();
		writeSet = new HashMap<>();
	}
	
	public void run() {
		while(true) {
			start();
			doTransaction();
			if (aborted) {
				aborted = false;
			} else {
				break;
			}
		}
	}
	
	public void start() {
		if (f.getCounter() % 2 == 1) {
			start = f.getCounter() - 1;
		} else {
			start = f.getCounter();
		}
	}
	
	public int read(int i) {
		if (aborted) return -1;
		System.out.println("helper reads");
		
		if (writeSet.containsKey(i)) {
			return writeSet.get(i);
		}
		int val = f.getData(i);
		if (f.getDirty(i) > start) {
			abort();
			return -1;
		}
		readSet.add(i);
		return val;
	}
	
	public void write(int i, int n) {
		System.out.println("helper writes");
		if (aborted) return;
		
		if (f.getDirty(i) > start) {
			abort();
			return;
		}
		writeSet.put(i, n);
	}
	
	public boolean validate() {
		if (aborted) return false;
		
		if (f.getCounter() <= start) {
			return true;
		}
		for (Integer i : readSet) {
			if (f.getDirty(i) > start) {
				return false;
			}
		}
		for (Integer i : writeSet.keySet()) {
			if (f.getDirty(i) > start) {
				return false;
			}
		}
		return true;
	}
	
	public void commit() {
		if (aborted || writeSet.isEmpty()) {
			return;
		}
		
		f.lockHelpers();
		f.lockMaster();
		
		if (!validate()) {
			f.unlockMaster();
			f.unlockHelpers();
			abort();
			return;
		}
		
		f.incrementCounter();
		
		for (Integer i : writeSet.keySet()) {
			f.setDirty(i, f.getCounter());
			f.setData(i, writeSet.get(i));
		}
		
		f.incrementCounter();
		
		f.unlockMaster();
		f.unlockHelpers();
	}
	
	public void abort() {
		writeSet = new HashMap<>();
		readSet = new HashSet<>();
		aborted = true;
	}

}
