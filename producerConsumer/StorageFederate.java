package hla13.producerConsumer;


import hla.rti.AttributeHandleSet;
import hla.rti.LogicalTime;
import hla.rti.RTIexception;
import hla.rti.SuppliedAttributes;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import hla13.Federate;

public class StorageFederate extends Federate {

    protected StorageAmbassador storageAmbassador;
    private int stock = 10;
    private int storageHlaHandle;

    @Override
    protected void initialize() throws RTIexception {
        registerStorageObject();
    }

    @Override
    protected StorageAmbassador createAmbassador() {
        storageAmbassador = new StorageAmbassador();
        return storageAmbassador;
    }

    @Override
    protected void tick() throws RTIexception {
        double timeToAdvance = fedAmb.federateTime + timeStep;
        advanceTime(timeStep);

        if (storageAmbassador.externalEvents.size() > 0) {
            storageAmbassador.externalEvents.sort(new ExternalEvent.ExternalEventComparator());
            for (ExternalEvent externalEvent : storageAmbassador.externalEvents) {
                fedAmb.federateTime = externalEvent.getTime();
                switch (externalEvent.getEventType()) {
                    case ADD:
                        this.addToStock(externalEvent.getQty());
                        break;

                    case GET:
                        this.getFromStock(externalEvent.getQty());
                        break;
                }
            }
            storageAmbassador.externalEvents.clear();
        }

        if (storageAmbassador.grantedTime == timeToAdvance) {
            timeToAdvance += storageAmbassador.federateLookahead;
            log("Updating stock at time: " + timeToAdvance);
            updateHLAObject(timeToAdvance);
            storageAmbassador.federateTime = timeToAdvance;
        }
    }

    public void addToStock(int qty) {
        this.stock += qty;
        log("Added " + qty + " at time: " + storageAmbassador.federateTime + ", current stock: " + this.stock);
    }

    public void getFromStock(int qty) {
        if (this.stock - qty < 0) {
            log("Not enough product at stock");
        } else {
            this.stock -= qty;
            log("Removed " + qty + " at time: " + storageAmbassador.federateTime + ", current stock: " + this.stock);
        }
    }

    private void registerStorageObject() throws RTIexception {
        int classHandle = rtiAmb.getObjectClassHandle("ObjectRoot.Storage");
        this.storageHlaHandle = rtiAmb.registerObjectInstance(classHandle);
    }

    private void updateHLAObject(double time) throws RTIexception {
        SuppliedAttributes attributes = RtiFactoryFactory.getRtiFactory().createSuppliedAttributes();

        int classHandle = rtiAmb.getObjectClass(storageHlaHandle);
        int stockHandle = rtiAmb.getAttributeHandle("stock", classHandle);
        byte[] stockValue = EncodingHelpers.encodeInt(stock);

        attributes.add(stockHandle, stockValue);
        LogicalTime logicalTime = convertTime(time);
        rtiAmb.updateAttributeValues(storageHlaHandle, attributes, "actualize stock".getBytes(), logicalTime);
    }

    protected void publishAndSubscribe() throws RTIexception {
        int classHandle = rtiAmb.getObjectClassHandle("ObjectRoot.Storage");
        int stockHandle = rtiAmb.getAttributeHandle("stock", classHandle);

        AttributeHandleSet attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
        attributes.add(stockHandle);

        rtiAmb.publishObjectClass(classHandle, attributes);

        int addProductHandle = rtiAmb.getInteractionClassHandle("InteractionRoot.AddProduct");
        storageAmbassador.addProductHandle = addProductHandle;
        rtiAmb.subscribeInteractionClass(addProductHandle);

        int getProductHandle = rtiAmb.getInteractionClassHandle("InteractionRoot.GetProduct");
        storageAmbassador.getProductHandle = getProductHandle;
        rtiAmb.subscribeInteractionClass(getProductHandle);
    }

    public static void main(String[] args) {
        try {
            new StorageFederate().runFederate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
