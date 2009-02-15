import java.util.*;

/*
 * file:        Q3.java
 * description: Barbershop object for homework 2, question 3
 * 
 * Peter Desnoyers, Northeastern CCIS, 2009
 * $Id: Q3.java 11 2009-02-02 13:15:04Z pjd $
 */

/*
 * Your answers go here.
 */
public class Q4Impl{
    List<Integer> chairs;
    int numChairs;
    
    public Q4Impl(){
        //Use a synchronized list for add, size, and removal
        chairs = Collections.synchronizedList(new LinkedList<Integer>());
        numChairs = 5;
    }
    
    public Q4Impl(int numChairs){
        chairs = Collections.synchronizedList(new LinkedList<Integer>());
        this.numChairs = numChairs;
    }
    
    /*
     * method tries to add a customer to the queue (the chairs)
     */
    public void customer(int id) {
        System.out.println("Customer " + id + " has entered the store");
        //If there is an available chair
        if(chairs.size() <= numChairs){
            //Add the customer to the queue
            chairs.add(id);
            Simulatable.simNotifyAll(this);
        }else{
            //Otherwise the customer just leaves the store
            System.out.println("Customer " + id + " left the store becasue" +
            		" it was full");
        }
    }
    
    public void barber() {
        //Continue to loop perpetually
        while(true){
            //If nobody is in the queue
            if(chairs.size() == 0){
                    try {
                        System.out.println("Nobody is in the store.  The " +
                        		"barber is going to sleep.");
                        Simulatable.simWait(this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }else{
                //Always remove from the head to get FIFO
                int customerID = chairs.remove(0); 
                System.out.println(queueToString(chairs));
                System.out.println("Cutting customer " + customerID + "'s" +
                		" hair...");
                
                try {
                    //Variate the time it takes to cut someone's hair
                    //Thread.sleep((int)Variate.exponential(0.1)*100);
                    Simulatable.simDelay((int)Variate.exponential(0.1)*100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                System.out.println("Finished cutting customer " + customerID +
                        "'s hair");
            }
        }
        
    }
    
    //Helper method that returns a string representation of the customers
    //waiting on the queue.
    private String queueToString(List<Integer> list){
        StringBuilder sb = new StringBuilder().append("Queue: {");
        for(Integer intt : list){
            sb.append(intt + " ");
        }
        sb.append("}");
        return sb.toString();
    }
}
