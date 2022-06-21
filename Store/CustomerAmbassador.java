package hla13.Store;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.ReceivedInteraction;
import hla.rti.jlc.EncodingHelpers;
import hla13.Ambassador;

import static hla13.Store.CustomerAmbassador.Status.*;

public class CustomerAmbassador extends Ambassador {
    public enum Status {SHOPPING, LOOKING_FOR_QUEUE, WAITING_TO_JOIN_QUEUE, IN_QUEUE}

    public Status status = SHOPPING;
    private int queueId;
    public int id;
    private boolean isPrivileged;

    public CustomerAmbassador(int id) {
        super();
        this.id = id;
    }

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
        try {
            int customerId = EncodingHelpers.decodeInt(theInteraction.getValue(1));
            if (this.id == customerId) {
                int queueId = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                receiveInteraction(interactionHandleMap.get(interactionClass), queueId);
            }
        } catch (ArrayIndexOutOfBounds e) {
//            throw new RuntimeException(e);
        }
    }

    public void receiveInteraction(String interactionName, int queueId) {
        switch (interactionName) {
            case "serving_complete":
                System.out.println("Customer Finished Shopping");
//                running = false;
                break;
            case "payment_failure":
                if (this.queueId == queueId) {
                    this.queueId = -1;
                    status = LOOKING_FOR_QUEUE;
                }
                break;
            case "join_queue":
                status = IN_QUEUE;
                this.queueId = queueId;
                break;
        }
    }
}
