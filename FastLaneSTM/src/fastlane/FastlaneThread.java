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
			if (f.getMasterID() == this.getId()) {
				// goto master codepath
				if (!f.getMasterCP().isEmpty()) {
					new Thread(f.getMasterCP().get(0)).start();
					f.getMasterCP().remove(0);
				} else {
					f.unlockMaster();
					return;
				}
			} else {
				f.unlockMaster();
				// goto helper codepath
				if (!f.getHelpersCP().isEmpty()) {
					new Thread(f.getHelpersCP().get(0)).start();
					f.getHelpersCP().remove(0);
				} else {
					return;
				}
			}
		} else if (pessimistic) {
			f.lockMaster();
			f.setMasterID(this.getId());
			// goto master codepath
			if (!f.getMasterCP().isEmpty()) {
				new Thread(f.getMasterCP().get(0)).start();
				f.getMasterCP().remove(0);
			} else {
				f.unlockMaster();
				return;
			}
		} else {
			// goto helper codepath
			if (!f.getHelpersCP().isEmpty()) {
			new Thread(f.getHelpersCP().get(0)).start();
			f.getHelpersCP().remove(0);
			} else {
				return;
			}
		}
	}

	public void run() {

		while (!f.getMasterCP().isEmpty() && !f.getHelpersCP().isEmpty()) {
			startExecution(true);
		}
	}
}
