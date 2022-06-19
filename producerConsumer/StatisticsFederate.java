package hla13.producerConsumer;

import hla.rti.*;
import hla.rti.jlc.RtiFactoryFactory;
import hla13.Federate;
import hla13.HandlersHelper;

import javax.management.InvalidAttributeValueException;

public class StatisticsFederate extends Federate {
    protected StatisticsAmbassador statisticsAmbassador;

    @Override
    public void runFederate() throws RTIexception, InvalidAttributeValueException {
        super.runFederate();
        destroyFederation();
    }

    @Override
    protected StatisticsAmbassador createAmbassador(){
        statisticsAmbassador = new StatisticsAmbassador();
        return statisticsAmbassador;
    }

    @Override
    protected void tick() throws RTIexception {
    }

    protected void publishAndSubscribe() throws RTIexception {
        // Publkowanie obiektu SimObject z atrybutem state
        int simObjectClassHandle = rtiAmb.getObjectClassHandle("ObjectRoot.Storage");
        int stateHandle = rtiAmb.getAttributeHandle("stock", simObjectClassHandle);

        AttributeHandleSet attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
        attributes.add(stateHandle);

        rtiAmb.subscribeObjectClassAttributes(simObjectClassHandle, attributes);

        // Zapisanie do interakcji konczacej
        int interactionHandle = rtiAmb.getInteractionClassHandle("InteractionRoot.Finish");
        // Dodanie mapowania interakcji na uchwyt
        HandlersHelper.addInteractionClassHandler("InteractionRoot.Finish", interactionHandle);

        rtiAmb.subscribeInteractionClass(interactionHandle);
    }

    public static void main(String[] args) {
        try {
            new StatisticsFederate().runFederate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
