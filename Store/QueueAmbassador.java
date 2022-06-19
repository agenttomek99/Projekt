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
        privilegedCustomerIds.removeIf(cId -> cId == id);
        customerIds.removeIf(cId -> cId == id);
        System.out.println();
    }
}
