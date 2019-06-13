package fastlane;

public class FastlaneThread extends Thread {
	
	private Framework f;
	
	public FastlaneThread(Framework fw) {
		f = fw;
	}
	
	public long getID() {
		return this.getId();
	}
	
	public void startExecution(boolean pessimistic) {
		if (f.getMasterID() == this.getId()) {
			f.lockMaster();
			if (f.getMasterID() == this.getId()) {
			} else {
				f.unlockMaster();
				// goto helper codepath
				new Thread(f.getHelpersCP().get(0)).start();
				f.getHelpersCP().remove(0);
			}
		} else if (pessimistic) {
			f.lockMaster();
			f.setMasterID(this.getId());
			// goto master codepath
			new Thread(f.getMasterCP().get(0)).start();
			f.getMasterCP().remove(0);
		} else {
			// goto helper codepath
			new Thread(f.getHelpersCP().get(0)).start();
			f.getHelpersCP().remove(0);
		}
	}
	
	public void run() {
		while (!f.getMasterCP().isEmpty() || !f.getHelpersCP().isEmpty()) {
			startExecution(true);
		}	
	}
}
