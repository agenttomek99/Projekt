package hla13.Store;

import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;

import java.util.ArrayList;

public class StatisticsAmbassador extends QueueBasedAmbassador {

    StatisticsAmbassador(int queueId) {
        super(queueId);
    }

    private ArrayList<Integer> queueLengthHistory = new ArrayList<>();

    private int currentQueueLength = 0;

    private boolean isTerminalWorking = true;

    int time = 0;




    @Override
    public void receiveInteraction(String interactionName, ReceivedInteraction theInteraction) {
        ++time;
        if (interactionName.equals("terminal_failure")){
            this.isTerminalWorking = false;
            currentQueueLength = 0;
            queueLengthHistory.add(new Integer(0));
            log("Terminal failed");
        }
        if (interactionName.equals("terminal_fixed")){
            this.isTerminalWorking = true;
            log("Terminal fixed");
        }
        if (interactionName.equals("payment_complete") && isTerminalWorking){
            currentQueueLength = currentQueueLength > 0 ? --currentQueueLength : 0;
            queueLengthHistory.add(new Integer(currentQueueLength));
        }
        if (interactionName.equals("join_queue") && isTerminalWorking){
            ++currentQueueLength;
            queueLengthHistory.add(new Integer(currentQueueLength));
        }
    }

    public int getPeopleInQueue() {
        return currentQueueLength ;
    }

    public int getQueueId() {
        return queueId;
    }

    public float getAverageQueueLength(){
        return queueLengthHistory.size() == 0 ? 0 : ((float) queueLengthHistory.stream().mapToInt(n->n).sum() / (float) time);
    }
}
