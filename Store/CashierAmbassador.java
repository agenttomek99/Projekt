package hla13.Store;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.ReceivedInteraction;
import hla.rti.jlc.EncodingHelpers;

import static hla13.Store.CashierAmbassador.Status.*;

public class CashierAmbassador extends QueueBasedAmbassador {
    public enum Status {FREE, SERVING, REQUEST_PAYMENT, WAITING_FOR_PAYMENT, PAYMENT_COMPLETE, TERMINAL_FAILURE, RESTARTING_TERMINAL}

    public Status status = FREE;
    int served_customer_id;

    CashierAmbassador(int queueId) {
        super(queueId);
    }

    @Override
    public void receiveInteraction(String interactionName, ReceivedInteraction theInteraction) {
        switch (interactionName) {
            case "service_request":
                try {
                    served_customer_id = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                    status = SERVING;
                    break;
                } catch (ArrayIndexOutOfBounds e) {
                    throw new RuntimeException(e);
                }
            case "payment_complete":
                served_customer_id = -1;
                status = FREE;
                break;
            case "payment_failure":
                served_customer_id = -1;
                status = TERMINAL_FAILURE;
                break;
        }
    }
}
