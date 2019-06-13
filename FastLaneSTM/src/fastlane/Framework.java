package fastlane;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Framework {
	private int cntr = 0;
	private int[] dirty;
	private int[] data;
	private ReentrantLock master;
	private ReentrantLock helpers;
	private long masterID = 0;

	private ArrayList<MasterTransaction> masterCP = new ArrayList<>();
	private ArrayList<HelpersTransaction> helpersCP = new ArrayList<>();

	private int currentMasterCtx = 0;
	private int currentHelpersCtx = 0;

	public Framework(int memSize) {
		dirty = new int[memSize];
		data = new int[memSize];
		master = new ReentrantLock();
		helpers = new ReentrantLock();
	}

	public int getCounter() {
		return cntr;
	}

	public void setCounter(int i) {
		cntr = i;
	}

	public void incrementCounter() {
		cntr++;
	}

	public int getDirty(int i) {
		return dirty[i];
	}

	public void setDirty(int i, int n) {
		dirty[i] = n;
	}

	public int getMemSize() {
		return data.length;
	}

	public int getData(int i) {
		return data[i];
	}

	public void setData(int i, int n) {
		data[i] = n;
	}

	public void lockMaster() {
		master.lock();
	}

	public void unlockMaster() {
		master.unlock();
	}

	public void lockHelpers() {
		helpers.lock();
	}

	public void unlockHelpers() {
		helpers.unlock();
	}

	public long getMasterID() {
		return masterID;
	}

	public void setMasterID(long id) {
		masterID = id;
	}

	public synchronized MasterTransaction getMasterCP() {
		if (masterCP.isEmpty()) {
			return null;
		} else {
			MasterTransaction t = masterCP.get(0);
			masterCP.remove(0);
			return t;
		}
	}

	public synchronized HelpersTransaction getHelpersCP() {
		if (helpersCP.isEmpty()) {
			return null;
		} else {
			HelpersTransaction t = helpersCP.get(0);
			helpersCP.remove(0);
			return t;
		}
	}

	public boolean isTransactionAvailable() {
		return !masterCP.isEmpty() && !helpersCP.isEmpty();
	}

	public void addTransaction(Transaction t) {
		if (t instanceof MasterTransaction) {
			masterCP.add((MasterTransaction) t);
		} else if (t instanceof HelpersTransaction) {
			helpersCP.add((HelpersTransaction) t);
		}
	}

	public int getCurrentMasterCtx() {
		return currentMasterCtx;
	}

	public int getCurrentHelpersCtx() {
		return currentHelpersCtx;
	}
}
