package hla13.Store;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.ReceivedInteraction;
import hla.rti.jlc.EncodingHelpers;

public class GuiAmbassador extends QueueBasedAmbassador{

    private int averageQueueLength;
    private int peopleInQueue;
    private int queueId;

    public int getAverageQueueLength() {
        return averageQueueLength;
    }

    public int getPeopleInQueue() {
        return peopleInQueue;
    }

    public int getQueueId() {
        return queueId;
    }

    GuiAmbassador(int queueId) {
        super(queueId);
    }

    @Override
    public void receiveInteraction(String interactionName, ReceivedInteraction theInteraction) {
        if (interactionName.equals("display_call"))
            try {
                this.queueId = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                this.peopleInQueue = EncodingHelpers.decodeInt(theInteraction.getValue(1));
                this.averageQueueLength = EncodingHelpers.decodeInt(theInteraction.getValue(2));

                if (peopleInQueue > 0 || averageQueueLength > 0){
                    log("Hello from queue: " + queueId + ", currently: " + peopleInQueue + " people in the queue");
                    log("Queue: " + queueId + ", average length: " + ((float)averageQueueLength)/100.0);
                }
            } catch (ArrayIndexOutOfBounds e) {
                throw new RuntimeException(e);
            }
    }
}
