package hla13.Store;

import hla.rti.*;
import hla.rti.jlc.RtiFactoryFactory;
import hla13.Federate;

import javax.management.InvalidAttributeValueException;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class StoreFederate extends Federate {
    public int COUNT_QUEUES = 2;
    private final double NEW_CUSTOMER_RATE = 0.1;
    private int customerCount = 0;
    private StoreAmbassador storeAmbassador;

    public StoreFederate createFederation() throws RTIinternalError, ErrorReadingFED, ConcurrentAccessAttempted, CouldNotOpenFED, MalformedURLException, FederationExecutionAlreadyExists {
        rtiAmb = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
        File fom = new File(fedFileName);
        rtiAmb.createFederationExecution("ExampleFederation", fom.toURI().toURL());
        log("Created Federation");
        return this;
    }

    @Override
    protected void initialize() {
        for (int queueId = 0; queueId < COUNT_QUEUES; queueId++) {
            storeAmbassador.queueMap.put(queueId, true);
            CashierFederate cashierFederate = new CashierFederate(queueId);
            QueueFederate queueFederate = new QueueFederate(queueId);
            TerminalFederate terminalFederate = new TerminalFederate(queueId);
            StatisticsFederate statisticsFederate = new StatisticsFederate(queueId);
            GuiFederate guiFederate = new GuiFederate(queueId);
            ApiFederate apiFederate = new ApiFederate(queueId);

            new Thread(() -> {
                try {
                    guiFederate.runFederate();
                } catch (RTIexception | InvalidAttributeValueException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            new Thread(() -> {
                try {
                    apiFederate.runFederate();
                } catch (RTIexception | InvalidAttributeValueException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            new Thread(() -> {
                try {
                    statisticsFederate.runFederate();
                } catch (RTIexception | InvalidAttributeValueException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            new Thread(() -> {
                try {
                    cashierFederate.runFederate();
                } catch (RTIexception | InvalidAttributeValueException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            new Thread(() -> {
                try {
                    queueFederate.runFederate();
                } catch (RTIexception | InvalidAttributeValueException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            new Thread(() -> {
                try {
                    terminalFederate.runFederate();
                } catch (RTIexception | InvalidAttributeValueException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    @Override
    protected StoreAmbassador createAmbassador() {
        storeAmbassador = new StoreAmbassador();
        return storeAmbassador;
    }

    @Override
    protected void tick() throws RTIexception, InvalidAttributeValueException {
        advanceTime(timeStep);

        ArrayList<Integer> availableQueues = getAvailableQueues();
        if (!availableQueues.isEmpty()) {
            for (int customerId : storeAmbassador.customersWaiting) {
                int assignedQueueId = availableQueues.get(new Random().nextInt(availableQueues.size()));
                sendInteraction("join_queue", assignedQueueId, customerId);
            }
        }
        storeAmbassador.customersWaiting.clear();

        // limiting the amount of incoming customers
        if (new Random().nextFloat() > NEW_CUSTOMER_RATE) return;

        CustomerFederate customerFederate = new CustomerFederate(++customerCount);
        new Thread(() -> {
            try {
                customerFederate.runFederate();
            } catch (RTIexception | InvalidAttributeValueException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    /* returns list of queueId for which the terminal is registered to be working at the moment */
    private ArrayList<Integer> getAvailableQueues() {
        ArrayList<Integer> availableQueues = new ArrayList<>();
        for (HashMap.Entry<Integer, Boolean> queue : storeAmbassador.queueMap.entrySet()) {
            if (queue.getValue()) {
                availableQueues.add(queue.getKey());
            }
        }
        return availableQueues;
    }

    @Override
    protected void publishAndSubscribe() throws RTIexception {
        publishAndSubscribe(new String[]{
                "join_queue",
        }, new String[]{
                "looking_for_queue",
                "terminal_fixed",
                "terminal_failure"
        });
    }

    public static void main(String[] args) {
        try {
            new StoreFederate().createFederation().runFederate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
