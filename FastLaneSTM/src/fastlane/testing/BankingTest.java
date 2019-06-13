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
				int i = read(1);
				int j = read(2);
				write(1, i + j);
				write(2, i - j);
			}
		});

		// set helpers CP
		fw.addTransaction(new HelpersTransaction(fw) {
			@Override
			public void doTransaction() {
				int i = read(1);
				write(1,i+40000);
			}
		});
		
		fw.addTransaction(new HelpersTransaction(fw) {

			@Override
			public void doTransaction() {
				write(0,0);
			}
			
		});
		
		FastlaneThread t1 = new FastlaneThread(fw);
		FastlaneThread t2 = new FastlaneThread(fw);
		
		t1.start();
		t2.start();
		
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertEquals(fw.getData(0),0);
		
		
		
	}

}
