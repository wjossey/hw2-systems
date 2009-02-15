/*
 * file:        Q4.java
 * description: Homework 2 Question 4 - event-driven simulation
 * 
 * Peter Desnoyers, Northeastern CCIS, 2009
 * $Id: Q4.java 14 2009-02-03 22:41:20Z pjd $
 */

public class Q4 extends Simulatable {
	int i;
	Q4Impl q4;
	public Q4(int i, Q4Impl q4) {
		this.i = i;
		this.q4 = q4;
	}
	public void simulate() throws InterruptedException {
		int j = 0;
		Statistic.States s = new Statistic.States("thread state " + Integer.toString(i));
		
		while (simRunning()) {
			double t = Variate.exponential(0.1);
			System.out.println("thread " + Integer.toString(i) + 
					" time " + Double.toString(getTime()) + " sleeping " +
							Double.toString(t));
			
			if (t > 100) {
				System.out.println("long delay: thread " + Integer.toString(i) + " value " + Double.toString(t));
			}
			if (t == 0.0) 
				System.out.println("thread " + Integer.toString(i) + " : 0 delay");
			simDelay(t);

			int state = j++ % 3;
			s.newState(state);
			q4.customer(i);
		}
	}
	
	public static void main(String[] args) {
	    Q4Impl q4 = new Q4Impl();
	    Simulatable.setStopTime(4000.0);
		for (int i = 0; i < 5; i++) {
			Thread t = new Thread(new Q4(i, q4));
			t.start();
		}
		q4.barber();
	}
}
