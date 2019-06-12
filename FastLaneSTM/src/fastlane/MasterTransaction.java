package fastlane;

public abstract class MasterTransaction {
	
	private Framework framework;
	
	public MasterTransaction (Framework f) {
		framework = f;
	}
	
	public abstract void doTransaction();

}
