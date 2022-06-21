package hla13.Store;

import hla.rti.RTIexception;
import hla13.Federate;

import javax.management.InvalidAttributeValueException;

public class GuiFederate extends Federate {

    private GuiAmbassador guiAmbassador;

    public GuiFederate(int queueId) {
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
                "display_call"
        }, new String[]{
                "information_call",
                "display_call"
        });
    }
    @Override
    protected GuiAmbassador createAmbassador(){
        guiAmbassador = new GuiAmbassador(queueId);
        return guiAmbassador;
    }
}
