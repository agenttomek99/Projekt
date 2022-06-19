package hla13.Store;

import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.ReceivedInteraction;

import static hla13.Store.TerminalAmbassador.Status.FREE;
import static hla13.Store.TerminalAmbassador.Status.PROCESSING;

public class TerminalAmbassador extends QueueBasedAmbassador {
    TerminalAmbassador(int queueId) {
        super(queueId);
    }

    public enum Status {PROCESSING, FREE, COMPLETE}
    public Status status = FREE;

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
    }

    @Override
    public void receiveInteraction(String interactionName, ReceivedInteraction theInteraction) {
        if (interactionName.equals("request_payment")) {
            status = PROCESSING;
        }
    }
}