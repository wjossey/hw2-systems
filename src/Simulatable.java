/*
 * file:        Simulatable.java
 * description: Simple Java event-driven simulation skeleton.
 * 
 * Peter Desnoyers, Northeastern CCIS, 2009
 * $Id: Simulatable.java 12 2009-02-03 22:39:40Z pjd $
 */


import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;


public abstract class Simulatable extends Thread implements Comparable {

	public double  tNext;
	public boolean ready;
	public Object  waitingOn;
	
	// each simulated class needs to implement simulate() instead of run()
	//
	public void simulate() throws InterruptedException{}
	
	public void run() {
		try {
			SimScheduler.theScheduler.Register(this);
			simulate();
		} catch (InterruptedException e) {}
		SimScheduler.theScheduler.Leave(this);
	}
	
	public int compareTo(Object obj) {
		Simulatable sim = (Simulatable) obj;
		if (sim.tNext == tNext)
			return 0;
		else
			return (sim.tNext < tNext) ? 1 : -1;
	}

    public static void setStopTime(double t) {
	SimScheduler.theScheduler.stopTime = t;
    }
	
	public static double getTime() {
		return SimScheduler.theScheduler.getTime();
	}
	
	public static void simWait(Object target) throws InterruptedException {
		SimScheduler.theScheduler.Wait(target);
	}
	
	public static void simDelay(double interval) throws InterruptedException {
		SimScheduler.theScheduler.Delay(interval);
	}
	
	public static void simNotify(Object target) {
		SimScheduler.theScheduler.Notify(target, false);
	}
	
	public static void simNotifyAll(Object target) {
		SimScheduler.theScheduler.Notify(target, true);
	}

	public static boolean simRunning() {
		return SimScheduler.theScheduler.Running();
	}
}

class SimScheduler extends Thread {
	double time = 0.0;
	double stopTime = 0.0;
    
	LinkedList runList = new LinkedList();
	LinkedList waitList = new LinkedList();
	PriorityQueue delayList = new PriorityQueue();
	Simulatable current = null;
	LinkedList allThreads = new LinkedList();

	static SimScheduler theScheduler = new SimScheduler();
	Thread thread = null;
	
	public double getTime() {
		return time;
	}
	
	public boolean Running() {
		return time < stopTime;
	}
	
	synchronized void Leave(Simulatable obj) {
		current = null;
		notifyAll();
	}
	
	synchronized void Delay(double interval) throws InterruptedException  {
		Simulatable obj = current;
		//if (obj == null)
			//return;
		obj.tNext = time + interval;
		delayList.add(obj);
		current = null;
		
		notifyAll();
		while (current != obj) 
			wait();
	}
	
	synchronized void Wait(Object target) throws InterruptedException {
		Simulatable obj = current;
		obj.waitingOn = target;
		waitList.add(obj);
		current = null;
		notifyAll();
		while (current != obj) 
			wait();
	}
	
	synchronized void Notify(Object target, boolean all) {
		Iterator iter = waitList.iterator();
		while (iter.hasNext()) {
			Simulatable thread = (Simulatable) iter.next();
			if (thread.waitingOn == target) {
				iter.remove();
				runList.add(thread);
				if (!all)
					break;
			}
		}
	}
	
	synchronized void Register(Simulatable obj) throws InterruptedException {
		runList.add(obj);
		allThreads.add(obj);
		
		notifyAll();
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
		while (current != obj) 
			wait();
	}
	
	
	public void run() {
		while (time < stopTime) {
			Simulatable next = null;
			synchronized (this) {
				if (!runList.isEmpty()) {
					next = (Simulatable) runList.remove();
				}
				else if (!waitList.isEmpty()) {
					next = (Simulatable) waitList.remove();
				}
				else if (!delayList.isEmpty()) {
					next = (Simulatable) delayList.remove();
					time = next.tNext;
				}
				if (next != null) {
					current = next;
					notifyAll();
				}
				try {wait();} catch (Exception e) {}
			} 
		}
		Statistic.allReports();
		System.out.println("stopping...");
		synchronized (this){
			notifyAll();
		}
		Iterator iter = allThreads.iterator();
		while (iter.hasNext()) {
		    Thread t = (Thread) iter.next();
		    t.interrupt();
		    try {t.join();} catch (Exception e) {}
		}
		System.out.println("stopped.");
	}
}
