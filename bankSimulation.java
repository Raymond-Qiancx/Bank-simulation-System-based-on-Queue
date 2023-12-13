
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

// =================== define customer class ==================//
class Customer {
    int arrivalTime;
    int serviceTime;

    public Customer(int arrivalTime, int serviceTime) {
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

}

class BankWindow {

    boolean isFree;
    int serviceEndTime;

    public BankWindow() {
        this.isFree = true;
        this.serviceEndTime = 0;
    }

    public void serve(Customer customer, int currentTime) {
        this.isFree = false;
        this.serviceEndTime = currentTime + customer.serviceTime;
    }
}

public class bankSimulation {

    // =================== parameters ====================//
    private static Random random = new Random();
    private static final double[] arriveProb = { 0.1, 0.15, 0.1, 0.35, 0.25, 0.05 };
    private static final double[] serviceProb = { 0.25, 0.2, 0.4, 0.15 };
    private static final int work_startTime = 510;
    private static final int work_endTime = 990; // unit: min

    // =================== main method ===================//
    public static void main(String[] args) {

        Queue<Customer> customerQueue = new LinkedList<>();
        List<Integer> waittingTimes = new ArrayList<>();

        int currentTime = work_startTime;

        customerQueue.addAll(creatCustomers(work_startTime, work_endTime));

        BankWindow[] bankWindows = { new BankWindow(), new BankWindow() };

        while (!customerQueue.isEmpty()) {
            updateBankWindow(bankWindows, currentTime);

            if (!customerQueue.isEmpty()) {
                Customer customer = customerQueue.peek();
                if (customer.arrivalTime <= currentTime) {
                    customer = customerQueue.poll();
                    int waitTime = findAndServeCustomer(customer, bankWindows, currentTime);
                    waittingTimes.add(waitTime);

                    System.out.println(" Customer's arrival time is: " + to24hrTime(customer.arrivalTime) +
                            ", serve time is:  " +customer.serviceTime+ "min" +
                            ", waiting time is: " + waitTime + "min");
                }
            }

            currentTime++;
        }

    }

    // =========== method for creating class of customers =================//
    private static List<Customer> creatCustomers(int startTime, int endTime) {

        int currentTime = startTime;
        List<Customer> customers = new ArrayList<>();

        while (currentTime < endTime) {
            int arrivalTime = getNextRandomIndex(arriveProb);
            currentTime += arrivalTime;

            int serviceTime = getNextRandomIndex(serviceProb) + 1;
            Customer c = new Customer(currentTime, serviceTime);
            customers.add(c);
        }
        return customers;
    }

    // ============ random index generator ===============//
    private static int getNextRandomIndex(double[] prob) {
        double sum = 0;
        double randomValue = random.nextDouble();
        for (int i = 0; i < prob.length; i++) {
            sum += prob[i];
            if (randomValue < sum) {
                return i;
            }
        }
        return prob.length - 1;
    }

    private static void updateBankWindow(BankWindow[] bankWindows, int time) {
        for (BankWindow window : bankWindows) {
            if (!window.isFree && window.serviceEndTime < time) {
                window.isFree = true;
            }
        }
    }

    private static int findAndServeCustomer(Customer customer, BankWindow[] banlBankWindows, int currentTime) {
        for (BankWindow window : banlBankWindows) {
            if (window.isFree) {
                window.serve(customer, currentTime);
                return currentTime - customer.arrivalTime;
            }
        }

        // if all windows are all busy, then find the earlist window to wait
        int minWaitTime = Integer.MAX_VALUE;
        BankWindow selectedWindow = null;
        for (BankWindow window : banlBankWindows) {
            int waitTime = window.serviceEndTime - customer.arrivalTime;
            if (waitTime < minWaitTime) {
                minWaitTime = waitTime;
                selectedWindow = window;
            }
        }
        selectedWindow.serve(customer, customer.arrivalTime + minWaitTime);
        return minWaitTime;

    }

    // Converts minutes into 24hr time format (HH:mm)
    private static String to24hrTime(int timeInMinutes) {
        int hours = timeInMinutes / 60;
        int minutes =timeInMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

}