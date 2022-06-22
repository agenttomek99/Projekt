package hla13.Store;


import hla.rti.RTIexception;
import hla13.Federate;

import javax.management.InvalidAttributeValueException;

public class StatisticsFederate extends Federate {
    private StatisticsAmbassador statisticsAmbassador;
    private int queueId;

    public StatisticsFederate(int queueId) {
        this.queueId = queueId;
    }

    @Override
    protected StatisticsAmbassador createAmbassador() {
        statisticsAmbassador = new StatisticsAmbassador(queueId);
        return statisticsAmbassador;
    }

    @Override
    protected void tick() throws RTIexception, InvalidAttributeValueException {
        int parameters[] = {
                queueId,
                statisticsAmbassador.getPeopleInQueue(),
                (int)((statisticsAmbassador.getAverageQueueLength()*100.0))
        };
        sendInteraction("display_call", parameters);
        advanceTime(timeStep);
    }
    @Override
    protected String getName() {
        return this.getClass().getSimpleName() + this.queueId;
    }
    @Override
    protected void publishAndSubscribe() throws RTIexception {
        publishAndSubscribe(new String[]{
                "display_call",
                //"payment_failure",
                //"payment_complete",
                //"join_queue",
                //"terminal_failure",
                //"terminal_fixed",
        }, new String[]{
                "information_call",
                "display_call",
                "payment_failure",
                "payment_complete",
                "join_queue",
                "terminal_failure",
                "terminal_fixed",
        });
    }
}

