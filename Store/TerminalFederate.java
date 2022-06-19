package hla13.Store;

import hla.rti.RTIexception;
import hla13.Federate;

import javax.management.InvalidAttributeValueException;
import java.util.Random;

import static hla13.Store.TerminalAmbassador.Status.COMPLETE;
import static hla13.Store.TerminalAmbassador.Status.FREE;

public class TerminalFederate extends Federate {
    private TerminalAmbassador terminalAmbassador;
    private final double PROCESSING_PAYMENT_DELAY = 1.0d;
    private final double FAILURE_RATE = 0.0d;
    private int queueId;

    public TerminalFederate(int queueId) {
        this.queueId = queueId;
    }

    @Override
    protected TerminalAmbassador createAmbassador() {
        terminalAmbassador = new TerminalAmbassador(this.queueId);
        return terminalAmbassador;
    }

    @Override
    protected void tick() throws RTIexception, InvalidAttributeValueException {
        switch (terminalAmbassador.status) {
            case COMPLETE:
                String paymentStatus = new Random().nextFloat() > FAILURE_RATE ? "payment_failure" : "payment_complete";
                terminalAmbassador.status = FREE;
                sendInteraction(paymentStatus, queueId);
            case FREE:
                advanceTime(timeStep);
                break;
            case PROCESSING:
                terminalAmbassador.status = COMPLETE;
                advanceTime(PROCESSING_PAYMENT_DELAY);
                break;
        }
    }

    @Override
    protected String getName() {
        return this.getClass().getSimpleName() + this.queueId;
    }
    @Override
    protected void publishAndSubscribe() throws RTIexception {
        publishAndSubscribe(new String[]{
                "payment_failure"
        }, new String[]{
                "request_payment",
        });
    }
}
