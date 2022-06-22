package hla13.Store;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.ReceivedInteraction;
import hla.rti.jlc.EncodingHelpers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ApiAmbassador extends QueueBasedAmbassador{

    private int peopleInQueue;
    private int queueId;

    private int averageQueueLength;

    ApiAmbassador(int queueId) {
        super(queueId);
    }

    @Override
    public void receiveInteraction(String interactionName, ReceivedInteraction theInteraction) {
        if (interactionName.equals("api_call"))
            try {
                this.queueId = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                this.peopleInQueue = EncodingHelpers.decodeInt(theInteraction.getValue(1));
                this.averageQueueLength = EncodingHelpers.decodeInt(theInteraction.getValue(2));

                if (peopleInQueue > 0 && averageQueueLength > 0){
                    String urlString = "http://localhost:5000/Qid:" + String.valueOf(queueId) + "/Now:" + String.valueOf(peopleInQueue)  + "/Avg:" + String.valueOf(averageQueueLength/100.0) ;
                    URL url = new URL(urlString);
                    URLConnection conn = url.openConnection();
                    InputStream is = conn.getInputStream();
                }
                //log("Hello from queue: " + queueId + ", currently: " + peopleInQueue + " people in the queue.");
            } catch (ArrayIndexOutOfBounds e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                log("Cannot connect to the API");
            //throw new RuntimeException(e);
            }
    }
}
