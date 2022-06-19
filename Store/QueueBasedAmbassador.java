package hla13.Store;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.ReceivedInteraction;
import hla.rti.jlc.EncodingHelpers;
import hla13.Ambassador;

public abstract class QueueBasedAmbassador extends Ambassador {
    int queueId;

    QueueBasedAmbassador(int queueId) {
        this.queueId = queueId;
    }

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
        System.out.println("Received Interaction: " + interactionHandleMap.get(interactionClass));
        try {
            int queueId = EncodingHelpers.decodeInt(theInteraction.getValue(0));
            if (this.queueId == queueId) {
                receiveInteraction(interactionHandleMap.get(interactionClass), theInteraction);
            }
        } catch (ArrayIndexOutOfBounds e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void receiveInteraction(String interactionName, ReceivedInteraction theInteraction);
}
