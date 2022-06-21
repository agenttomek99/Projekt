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
        String interactionName = interactionHandleMap.get(interactionClass);
        try {
            int queueId = EncodingHelpers.decodeInt(theInteraction.getValue(0));
            if (this.queueId == queueId) {
                System.out.println(this.getClass().getSimpleName() + ": Received Interaction: " + interactionName + " Queue: " + queueId);
                receiveInteraction(interactionName, theInteraction);
            }
        } catch (ArrayIndexOutOfBounds ignored) {
        }
    }

    public abstract void receiveInteraction(String interactionName, ReceivedInteraction theInteraction);
}
