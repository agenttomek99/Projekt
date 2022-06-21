package hla13.Store;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.ReceivedInteraction;
import hla.rti.jlc.EncodingHelpers;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class QueueAmbassador extends QueueBasedAmbassador {
    public boolean isOpen = true;
    public boolean isServed = false;
    ArrayList<Integer> customerIds = new ArrayList<>();
    ArrayList<Integer> privilegedCustomerIds = new ArrayList<>();

    ArrayList<Integer> queueLengthHistory = new ArrayList<>();

    QueueAmbassador(int queueId) {
        super(queueId);
    }

    public void receiveInteraction(String interactionName, ReceivedInteraction theInteraction) {
        switch (interactionName) {
            case "serving_complete":
                isServed = false;
                break;
            case "payment_failure":
                isOpen = false;
                isServed = false;
                customerIds.clear();
                break;
            case "terminal_fixed":
                isOpen = true;
                break;
            case "join_queue":
                try {
                    int customerId = EncodingHelpers.decodeInt(theInteraction.getValue(1));
                    boolean isPrivileged = new Random().nextBoolean();
                    if (isPrivileged) {
                        privilegedCustomerIds.add(customerId);
                    } else {
                        customerIds.add(customerId);
                    }
                } catch (ArrayIndexOutOfBounds e) {
                    throw new RuntimeException(e);
                }
                queueLengthHistory.add(new Integer(customerIds.size() + privilegedCustomerIds.size()));
                break;
        }
    }

    public boolean isEmpty() {
        return privilegedCustomerIds.isEmpty() && customerIds.isEmpty();
    }

    public int getNextCustomer() {
        if (!privilegedCustomerIds.isEmpty()) {
            return privilegedCustomerIds.get(0);
        }
        if (!customerIds.isEmpty()) {
            return customerIds.get(0);
        }
        return -1;
    }

    public void removeCustomer(Integer id) {
        privilegedCustomerIds.removeIf(cId -> Objects.equals(cId, id));
        customerIds.removeIf(cId -> Objects.equals(cId, id));
        System.out.println();
    }

    public int getQueueLength(){
        return customerIds.size() + privilegedCustomerIds.size();
    }
    public float getAverageQueueLength(){
        return queueLengthHistory.size() == 0 ? 0 : ((float) queueLengthHistory.stream().mapToInt(n->n).sum() / (float) queueLengthHistory.size());
    }


}
