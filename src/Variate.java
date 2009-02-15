/*
 * file:        Variate.java
 * description: exponential variates.
 * 
 * Peter Desnoyers, Northeastern CCIS, 2009
 * $Id: Variate.java 11 2009-02-02 13:15:04Z pjd $
 */

import java.util.Random;

public class Variate {
    private static Random generator = new Random();
    
    public static double uniform(double a, double b) {
        return a + generator.nextDouble() * (b-a);
    }
    
    public static double exponential(double lambda) {
        return -1 * Math.log(generator.nextDouble()) / lambda;
    }
}
