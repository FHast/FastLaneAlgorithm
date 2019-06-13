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
				// goto helper codepath
					HelpersTransaction t = f.getHelpersCP();
					if (t != null) {
						t.run();
				} else {
					return;
				}
			}
		} else if (pessimistic) {
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

		while (f.isTransactionAvailable()) {
			startExecution(true);
		}
	}
}
