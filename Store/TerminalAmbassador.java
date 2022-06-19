package hla13.Store;

import hla.rti.ReceivedInteraction;

import static hla13.Store.TerminalAmbassador.Status.FREE;
import static hla13.Store.TerminalAmbassador.Status.PROCESSING;

public class TerminalAmbassador extends QueueBasedAmbassador {
    TerminalAmbassador(int queueId) {
        super(queueId);
    }

    public enum Status {PROCESSING, FREE, COMPLETE}

    public Status status = FREE;

    @Override
    public void receiveInteraction(String interactionName, ReceivedInteraction theInteraction) {
        if (interactionName.equals("request_payment")) {
            status = PROCESSING;
        }
    }
}