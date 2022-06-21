package hla13.Store;

import hla.rti.RTIexception;
import hla13.Federate;

import javax.management.InvalidAttributeValueException;

public class ApiFederate extends Federate {

    private ApiAmbassador apiAmbassador;

    public ApiFederate(int queueId) {
        this.queueId = queueId;
    }

    private int queueId;
    @Override
    protected void tick() throws RTIexception, InvalidAttributeValueException {
        advanceTime(timeStep);
    }
    @Override
    protected String getName() {
        return this.getClass().getSimpleName() + this.queueId;
    }
    @Override
    protected void publishAndSubscribe() throws RTIexception {
        publishAndSubscribe(new String[]{
                "information_call",
                "api_call"
        }, new String[]{
                "information_call",
                "api_call"
        });
    }
    @Override
    protected ApiAmbassador createAmbassador(){
        apiAmbassador = new ApiAmbassador(queueId);
        return apiAmbassador;
    }
}
