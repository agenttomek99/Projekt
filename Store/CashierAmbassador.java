package hla13.Store;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.ReceivedInteraction;
import hla.rti.jlc.EncodingHelpers;

import static hla13.Store.CashierAmbassador.Status.*;

public class CashierAmbassador extends QueueBasedAmbassador {
    public enum Status {FREE, SERVING, REQUEST_PAYMENT, WAITING_FOR_PAYMENT, PAYMENT_COMPLETE, TERMINAL_FAILURE, RESTARTING_TERMINAL}

    public Status status = FREE;
    int servedCustomerId;

    CashierAmbassador(int queueId) {
        super(queueId);
    }

    @Override
    public void receiveInteraction(String interactionName, ReceivedInteraction theInteraction) {
        switch (interactionName) {
            case "service_request":
                try {
                    servedCustomerId = EncodingHelpers.decodeInt(theInteraction.getValue(1));
                    status = SERVING;
                } catch (ArrayIndexOutOfBounds ignore) {
                }
                break;
            case "payment_complete":
                status = PAYMENT_COMPLETE;
                break;
            case "payment_failure":
                status = TERMINAL_FAILURE;
                break;
        }
    }
}
