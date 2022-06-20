package hla13.Store;

import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import hla13.Ambassador;
import hla13.HandlersHelper;

import static hla13.Store.CashierAmbassador.Status.SERVING;
import static hla13.Store.TerminalAmbassador.Status.PROCESSING;

public class StatisticsAmbassador extends QueueBasedAmbassador {

    StatisticsAmbassador(int queueId) {
        super(queueId);
    }



    @Override
    public void receiveInteraction(String interactionName, ReceivedInteraction theInteraction) {
        if (interactionName.equals("information_call"))
        try {
            int queueId = EncodingHelpers.decodeInt(theInteraction.getValue(0));
            int peopleInQueue = EncodingHelpers.decodeInt(theInteraction.getValue(1));
            log("Hello from queue: " + queueId + ", currently: " + peopleInQueue + " people in the queue.");
        } catch (ArrayIndexOutOfBounds e) {
//          throw new RuntimeException(e);
            log("ja pierdole wyjebalo sie");
        }
    }

}
