package hla13.producerConsumer;

import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import hla13.Federate;

import javax.management.InvalidAttributeValueException;
import java.util.Random;

/**
 * Created by Michal on 2016-04-27.
 */
public class ConsumerFederate extends Federate {

    private void sendInteraction(double timeStep) throws RTIexception {
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();
        Random random = new Random();
        int quantityInt = random.nextInt(10) + 1;
        byte[] quantity = EncodingHelpers.encodeInt(quantityInt);

        int interactionHandle = rtiAmb.getInteractionClassHandle("InteractionRoot.GetProduct");
        int quantityHandle = rtiAmb.getParameterHandle("quantity", interactionHandle);

        parameters.add(quantityHandle, quantity);

        LogicalTime time = convertTime(timeStep);
        log("Sending GetProduct: " + quantityInt);
        // TSO
        rtiAmb.sendInteraction(interactionHandle, parameters, "tag".getBytes(), time);
//        // RO
//        rtiamb.sendInteraction( interactionHandle, parameters, "tag".getBytes() );
    }

    @Override
    protected void tick() throws RTIexception {
        advanceTime(randomTime());
        sendInteraction(fedAmb.federateTime + fedAmb.federateLookahead);
    }

    protected void publishAndSubscribe() throws RTIexception {
        int addProductHandle = rtiAmb.getInteractionClassHandle("InteractionRoot.GetProduct");
        rtiAmb.publishInteractionClass(addProductHandle);
    }

    protected double randomTime() {
        return 1 + (4 * new Random().nextDouble());
    }

    public static void main(String[] args) throws InvalidAttributeValueException {
        try {
            new ConsumerFederate().runFederate();
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}
