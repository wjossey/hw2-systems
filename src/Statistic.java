/*
 * file:        Statistic.java
 * description: Objects for tracking statistics in a simulation.
 * 
 * Peter Desnoyers, Northeastern CCIS, 2009
 * $Id: Statistic.java 12 2009-02-03 22:39:40Z pjd $
 */


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Statistic {
	String desc;
	static LinkedList allStats = new LinkedList();
	
	public static void allReports() {
		Iterator iter = allStats.iterator();
		while (iter.hasNext()) {
			Statistic s = (Statistic) iter.next();
			if (s != null)
				s.report();
		}
	}
	
	public void report() {
	}
	
	public Statistic(String desc) {
		this.desc = desc;
		allStats.add(this);
	}
	
	/* 
	 * Calculate average occupancy of states over time.
	 */
	public static class States extends Categorical {
		int lastState = 0;
		double lastTime = 0.0;
		
		public States(String desc) {
			super(desc);
		}
		
		public void newState(int state) {
			double time = Simulatable.getTime();
			super.pairEvent(lastState, time-lastTime);
			lastState = state;
			lastTime = time;
		}
	}
	
	/*
	 * Statistics for a categorical variable.
	 */
	public static class Categorical extends Statistic {
		int n;
		double total;
		HashMap count = new HashMap();
		HashMap sum = new HashMap();
		
		public Categorical(String desc) {
			super(desc);
		}
		
		public void pairEvent(int _key, double val) {
			Integer key = new Integer(_key);
			n++;
			total += val;
			
			int old_count = 0;
			if (count.containsKey(key))
				old_count = ((Integer)count.get(key)).intValue();
			count.put(key, new Integer(old_count+1));
			
			double old_sum = 0.0;
			if (sum.containsKey(key))
				old_sum = ((Double)sum.get(key)).doubleValue();
			sum.put(key, new Double(old_sum+val));
		}
		
		public void report() {
			System.out.println(desc + ": n=" + Integer.toString(n) + 
					" total=" + Double.toString(total));
			Iterator iter = sum.keySet().iterator();
			while (iter.hasNext()) {
			        Integer key = (Integer) iter.next();
			        Double val = (Double) sum.get(key);
			        System.out.println("  key: " + key.toString() +
			        		" fraction: " + Double.toString(val.doubleValue() / total));
			}
		}
	}
	
	/*
	 * Stats for a quantitative variable - mean and standard deviation.
	 */
	public static class Quant extends Statistic {
		int n;
		double sum;
		double sum2;
		
		public Quant(String desc) {
			super(desc);
		}
		
		public void valEvent(double val) {
			n++;
			sum += val;
			sum2 += (val*val);
		}
		
		public void report() {
			double mean = sum / n;
			double var = (sum2 - (sum * sum) / n) / (n - 1);
			System.out.println(desc + ": n=" + Integer.toString(n) + " mean="
					+ Double.toString(mean) + 
					" std=" + Double.toString(Math.sqrt(var)));
		}
	}
}
