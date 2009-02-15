/*
 * file:        Q3Runner.java
 * description: Threads for homework 2, question 3
 * 
 * Peter Desnoyers, Northeastern CCIS, 2009
 * $Id: Q3Runner.java 11 2009-02-02 13:15:04Z pjd $
 */

public class Q3Runner implements Runnable {
    int index;
    Q3 q3;
    public Q3Runner(int index, Q3 q3) {
        this.index = index;
        this.q3 = q3;
    }

    public void run() {     
        while (true) { 
            // Java sleep() takes an integer # of milliseconds
            try { Thread.sleep((int)Variate.exponential(0.1)*1000); } catch (Exception e) {}
            q3.customer(index);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Q3 q3 = new Q3();
        for (int i = 0; i < 10; i++)
            (new Thread(new Q3Runner(i, q3))).start();
        q3.barber();
    }
}
