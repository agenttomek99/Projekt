package hla13.Store;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.ReceivedInteraction;
import hla.rti.jlc.EncodingHelpers;
import hla13.Ambassador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class StoreAmbassador  extends Ambassador {
    // keeps information about whether the queue is available or not
    HashMap<Integer, Boolean> queueMap = new HashMap<>();
    ArrayList<Integer> customersWaiting = new ArrayList<>();

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
        switch (interactionHandleMap.get(interactionClass)) {
            case "looking_for_queue":
                try {
                    int customerId = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                    customersWaiting.add(customerId);
                    System.out.println("StoreAmbassador: Received interaction: " + interactionHandleMap.get(interactionClass));
                } catch (ArrayIndexOutOfBounds e) {
//                    System.out.println("StoreAmbassador: Invalid interaction received:" + interactionHandleMap.get(interactionClass));
                }
                break;
            case "terminal_fixed":
            case "terminal_failure":
                int queueId = 0;
                try {
                    queueId = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                    queueMap.replace(queueId, interactionHandleMap.get(interactionClass).equals("terminal_fixed"));
                    System.out.println("StoreAmbassador: Received interaction: " + interactionHandleMap.get(interactionClass));
                } catch (ArrayIndexOutOfBounds e) {
//                    throw new RuntimeException(e);
                }
                break;
        }
    }
}
