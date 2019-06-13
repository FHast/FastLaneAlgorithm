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
				for(int i = 0; i != 3; i++) {
					write(i,5000);
				}
				commit();
			}
		});

		fw.addTransaction(new MasterTransaction(fw) {
			@Override
			public void doTransaction() {
				System.out.println("master2");
				int i = read(1);
				int j = read(2);
				write(1, i + j);
				write(2, 0);
				commit();
			}
		});
		
		fw.addTransaction(new MasterTransaction(fw) {
			@Override
			public void doTransaction() {
				System.out.println("master3");
				write(2, 0);
				commit();
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
				write(0,0);
				commit();
			}
			
		});
		
		FastlaneThread t1 = new FastlaneThread(fw,1);
		FastlaneThread t2 = new FastlaneThread(fw,2);
		FastlaneThread t3 = new FastlaneThread(fw,3);
		
		fw.setMasterID(1);
		
		t1.start();
		t2.start();
		t3.start();
		
		try {
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("data0 = " + fw.getData(0));
		System.out.println("data1 = " + fw.getData(1));
		System.out.println("data2 = " + fw.getData(2));
		
	}

}
