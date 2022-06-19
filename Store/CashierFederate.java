package hla13.Store;

import hla.rti.RTIexception;
import hla13.Federate;

import javax.management.InvalidAttributeValueException;

import static hla13.Store.CashierAmbassador.Status.*;


public class CashierFederate extends Federate {
    private final double TERMINAL_RESTART_DELAY = 3.0d;
    protected CashierAmbassador cashierAmbassador;
    private final int queueId;

    public CashierFederate(int queueId) {
        this.queueId = queueId;
    }

    @Override
    protected CashierAmbassador createAmbassador() {
        cashierAmbassador = new CashierAmbassador(queueId);
        return cashierAmbassador;
    }

    @Override
    protected String getName() {
        return this.getClass().getSimpleName() + this.queueId;
    }

    @Override
    protected void tick() throws RTIexception, InvalidAttributeValueException {
        switch (cashierAmbassador.status) {
            case RESTARTING_TERMINAL:
                sendInteraction("terminal_fixed", queueId);
            case FREE:
            case WAITING_FOR_PAYMENT:
                advanceTime(timeStep);
                break;
            case SERVING:
                cashierAmbassador.status = REQUEST_PAYMENT;
                advanceTime(getServingDelay());
                break;
            case REQUEST_PAYMENT:
                sendInteraction("request_payment", queueId);
                cashierAmbassador.status = WAITING_FOR_PAYMENT;
                advanceTime(timeStep);
                break;
            case PAYMENT_COMPLETE:
                cashierAmbassador.status = FREE;
                sendInteraction("serving_complete", queueId, cashierAmbassador.served_customer_id);
                break;
            case TERMINAL_FAILURE:
                sendInteraction("terminal_failure", queueId);
                cashierAmbassador.status = RESTARTING_TERMINAL;
                advanceTime(TERMINAL_RESTART_DELAY);
                break;
        }
    }

    private double getServingDelay() {
        return 3d;
    }


    @Override
    protected void publishAndSubscribe() throws RTIexception {
        publishAndSubscribe(new String[]{
                "terminal_fixed",
                "request_payment",
                "serving_complete",
                "terminal_failure",
        }, new String[]{
                "service_request",
                "payment_complete",
                "payment_failure"
        });
    }
}
