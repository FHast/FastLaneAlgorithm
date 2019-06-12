package fastlane;

public abstract class FastlaneThread extends Thread {
	private Framework framework;
	
	public FastlaneThread(Framework f) {
		framework = f;
	}
	
	public void startTransaction(boolean pessimistic) {
		if (framework.getMasterID() == this.getId()) {
			framework.lockMaster();
			if (framework.getMasterID() == this.getId()) {
				
			}
		} else if (pessimistic) {
			framework.lockMaster();
			
		} else {
			
		}
	}
	
	public void run() {
		
	}
	
	protected abstract void doMasterCP();
	protected abstract void doHelperCP();
}
