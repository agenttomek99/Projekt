package hla13.producerConsumer;


import hla.rti.LogicalTime;
import hla.rti.RTIexception;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import hla13.Federate;

import javax.management.InvalidAttributeValueException;
import java.util.Random;

public class ProducerFederate extends Federate {

    private void sendInteraction(double timeStep) throws RTIexception {
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();
        Random random = new Random();
        byte[] quantity = EncodingHelpers.encodeInt(random.nextInt(10) + 1);

        int interactionHandle = rtiAmb.getInteractionClassHandle("InteractionRoot.AddProduct");
        int quantityHandle = rtiAmb.getParameterHandle("quantity", interactionHandle);

        parameters.add(quantityHandle, quantity);

        LogicalTime time = convertTime(timeStep);
        rtiAmb.sendInteraction(interactionHandle, parameters, "tag".getBytes(), time);
    }

    protected void publishAndSubscribe() throws RTIexception {
        int addProductHandle = rtiAmb.getInteractionClassHandle("InteractionRoot.AddProduct");
        rtiAmb.publishInteractionClass(addProductHandle);
    }

    @Override
    protected void tick() throws RTIexception {
        this.advanceTime(randomTime());
        this.sendInteraction(fedAmb.federateTime + fedAmb.federateLookahead);
    }

    public static void main(String[] args) throws InvalidAttributeValueException {
        try {
            new ProducerFederate().runFederate();
        } catch (RTIexception rtie) {
            rtie.printStackTrace();
        }
    }
}
