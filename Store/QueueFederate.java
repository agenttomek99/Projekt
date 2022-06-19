package hla13.Store;

import hla.rti.RTIexception;
import hla13.Federate;

import javax.management.InvalidAttributeValueException;

public class QueueFederate extends Federate {
    private QueueAmbassador queueAmbassador;
    private int queueId;

    public QueueFederate(int queueId) {
        this.queueId = queueId;
    }

    @Override
    protected QueueAmbassador createAmbassador() {
        queueAmbassador = new QueueAmbassador(queueId);
        return queueAmbassador;
    }

    @Override
    protected void tick() throws RTIexception, InvalidAttributeValueException {
        if (queueAmbassador.isServed || queueAmbassador.isEmpty()) {
            advanceTime(timeStep);
            return;
        }
        if (queueAmbassador.isOpen) {
            sendInteraction("service_request", queueAmbassador.getNextCustomer());
        }
    }
    @Override
    protected String getName() {
        return this.getClass().getSimpleName() + this.queueId;
    }
    @Override
    protected void publishAndSubscribe() throws RTIexception {
        publishAndSubscribe(new String[]{
                "service_request",
        }, new String[]{
                "serving_complete",
                "payment_failure",
                "terminal_fixed",
                "join_queue"
        });
    }
}
