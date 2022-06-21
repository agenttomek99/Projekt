package hla13.Store;

import hla.rti.RTIexception;
import hla13.Federate;

import javax.management.InvalidAttributeValueException;

import static hla13.Store.CustomerAmbassador.Status.LOOKING_FOR_QUEUE;
import static hla13.Store.CustomerAmbassador.Status.WAITING_TO_JOIN_QUEUE;

public class CustomerFederate extends Federate {
    private final int id;
    private CustomerAmbassador customerAmbassador;

    public CustomerFederate(int id) {
        super();
        this.id = id;
    }

    @Override
    protected CustomerAmbassador createAmbassador() {
        customerAmbassador = new CustomerAmbassador(id);
        return customerAmbassador;
    }

    @Override
    protected void tick() throws RTIexception, InvalidAttributeValueException {
        switch (customerAmbassador.status) {
            case SHOPPING:
                advanceTime(getShoppingTime());
                customerAmbassador.status = LOOKING_FOR_QUEUE;
                break;
            case LOOKING_FOR_QUEUE:
                sendInteraction("looking_for_queue", customerAmbassador.id);
                customerAmbassador.status = WAITING_TO_JOIN_QUEUE;
                break;
            case WAITING_TO_JOIN_QUEUE:
            case IN_QUEUE:
                advanceTime(timeStep);
                break;
        }
    }

    @Override
    protected String getName() {
        return this.getClass().getSimpleName() + this.id;
    }

    private double getShoppingTime() {
        return timeStep;
    }

    @Override
    protected void publishAndSubscribe() throws RTIexception {
        publishAndSubscribe(new String[]{
                "looking_for_queue",
        }, new String[]{
                "serving_complete",
                "payment_failure",
                "join_queue"
        });
    }
}
