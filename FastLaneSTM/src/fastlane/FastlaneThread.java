package fastlane;

public class FastlaneThread extends Thread {

	private Framework f;
	private int tid;

	public FastlaneThread(Framework fw, int id) {
		f = fw;
		tid = id;
	}

	public int getID() {
		return tid;
	}

	public void startExecution(boolean pessimistic) {
		System.out.println("Master " + f.getMasterID() + ", Self " + tid);
		if (f.getMasterID() == tid) {
			f.lockMaster();
			if (f.getMasterID() == tid) {
				// goto master codepath
				MasterTransaction t = f.getMasterCP();
				if (t != null) {
					t.run();
				} else {
					f.unlockMaster();
					return;
				}
			} else {
				f.unlockMaster();
				System.out.println("helper");
				// goto helper codepath
				HelpersTransaction t = f.getHelpersCP();
				if (t != null) {
					t.run();
				} else {
					return;
				}
			}
		} else if (pessimistic) {
			System.out.println("pessimistic");
			f.lockMaster();
			f.setMasterID(tid);
			// goto master codepath
			MasterTransaction t = f.getMasterCP();
			if (t != null) {
				t.run();
			} else {
				f.unlockMaster();
				return;
			}
		} else {
			System.out.println("helper2");
			// goto helper codepath
			HelpersTransaction t = f.getHelpersCP();
			if (t != null) {
				t.run();
			} else {
				return;
			}
		}
	}

	public void run() {
		System.out.println("Start thread " + tid);
		System.out.println("mastercp no. = " + f.countMasterCP());
		System.out.println("helperscp no. = " + f.countHelpersCP());
		//System.out.println("transaction available: " + f.isTransactionAvailable());
		
		while(f.isTransactionAvailable()) {
			startExecution(false);
		}
	}
}
