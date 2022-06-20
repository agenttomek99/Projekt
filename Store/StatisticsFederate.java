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
        advanceTime(3d);
    }
    @Override
    protected String getName() {
        return this.getClass().getSimpleName() + this.queueId;
    }
    @Override
    protected void publishAndSubscribe() throws RTIexception {
        publishAndSubscribe(new String[]{
                "information_call"
        }, new String[]{
                "information_call"
        });
    }
}
