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

    ApiAmbassador(int queueId) {
        super(queueId);
    }

    @Override
    public void receiveInteraction(String interactionName, ReceivedInteraction theInteraction) {
        if (interactionName.equals("api_call"))
            try {
                this.queueId = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                this.peopleInQueue = EncodingHelpers.decodeInt(theInteraction.getValue(1));
                String urlString = "http://localhost:5000/data/";
                urlString += (this.queueId + this.peopleInQueue);

                URL url = null;
                try {
                    url = new URL(urlString);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                URLConnection conn = null;
                try {
                    InputStream is = conn.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //log("Hello from queue: " + queueId + ", currently: " + peopleInQueue + " people in the queue.");
            } catch (ArrayIndexOutOfBounds e) {
//          throw new RuntimeException(e);
                log("ja pierdole wyjebalo sie");
            }
    }
}
