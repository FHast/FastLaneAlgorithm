package fastlane.testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fastlane.*;

class BankingTest {

	@Test
	void test() {
		Framework fw = new Framework(3);

		// set data
		fw.setData(0, 500);
		fw.setData(1, 1000);
		fw.setData(2, 1500);

		// set master CP
		fw.addTransaction(new MasterTransaction(fw) {
			@Override
			public void doTransaction() {
				System.out.println("master");
				int i = read(1);
				int j = read(2);
				write(1, i + j);
				write(2, i - j);
				//commit();
			}
		});

		// set helpers CP
		fw.addTransaction(new HelpersTransaction(fw) {
			@Override
			public void doTransaction() {
				int i = read(1);
				write(1,i+40000);
				commit();
			}
		});
		
		fw.addTransaction(new HelpersTransaction(fw) {

			@Override
			public void doTransaction() {
				System.out.println("second helper");
				write(0,0);
				commit();
			}
			
		});
		
		FastlaneThread t1 = new FastlaneThread(fw,1);
		FastlaneThread t2 = new FastlaneThread(fw,2);
		
		fw.setMasterID(1);
		
		t1.start();
		t2.start();
		
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("data0 = " + fw.getData(0));
		System.out.println("data1 = " + fw.getData(1));
		System.out.println("data2 = " + fw.getData(2));
		
	}

}
