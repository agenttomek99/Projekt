package hla13.producerConsumer;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.ReceivedInteraction;
import hla.rti.jlc.EncodingHelpers;
import hla13.Ambassador;

import java.util.ArrayList;


public class StorageAmbassador extends Ambassador {
    public double grantedTime = 0.0;
    public int addProductHandle = 0;
    public int getProductHandle = 0;

    protected ArrayList<ExternalEvent> externalEvents = new ArrayList<>();

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag) {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
        receiveInteraction(interactionClass, theInteraction, tag, null, null);
    }

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
        StringBuilder builder = new StringBuilder("Interaction Received:");
        if (interactionClass == addProductHandle) {
            try {
                int qty = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                double time = convertTime(theTime);
                externalEvents.add(new ExternalEvent(qty, ExternalEvent.EventType.ADD, time));
                builder.append("AddProduct , time=").append(time);
                builder.append(" qty=").append(qty);
                builder.append("\n");
            } catch (ArrayIndexOutOfBounds ignored) {
            }

        } else if (interactionClass == getProductHandle) {
            try {
                int qty = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                double time = convertTime(theTime);
                externalEvents.add(new ExternalEvent(qty, ExternalEvent.EventType.GET, time));
                builder.append("GetProduct , time=").append(time);
                builder.append(" qty=").append(qty);
                builder.append("\n");

            } catch (ArrayIndexOutOfBounds ignored) {
            }
        }
        this.log(builder.toString());
    }
}
