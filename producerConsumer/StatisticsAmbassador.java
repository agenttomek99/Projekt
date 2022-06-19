package hla13.producerConsumer;

import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import hla13.Ambassador;
import hla13.HandlersHelper;

public class StatisticsAmbassador extends Ambassador {

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag) {
        receiveInteraction(interactionClass, theInteraction, tag, null, null);
    }

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
        StringBuilder builder = new StringBuilder("Interaction Received: ");

        if (interactionClass == HandlersHelper.getInteractionHandleByName("InteractionRoot.Finish")) {
            builder.append("Odebrano interakcję kończącą.");
            running = false;
        }

        log(builder.toString());
    }

    public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] tag) {
        reflectAttributeValues(theObject, theAttributes, tag, null, null);
    }

    public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] tag, LogicalTime theTime, EventRetractionHandle retractionHandle) {
        StringBuilder builder = new StringBuilder("Reflection for object:");

        builder.append(" handle=").append(theObject);
//		builder.append(", tag=" + EncodingHelpers.decodeString(tag));

        // print the attribute information
        builder.append(", attributeCount=").append(theAttributes.size());
        builder.append("\n");
        for (int i = 0; i < theAttributes.size(); i++) {
            try {
                // print the attibute handle
                builder.append("\tattributeHandle=");
                builder.append(theAttributes.getAttributeHandle(i));
                // print the attribute value
                builder.append(", attributeValue=");
                builder.append(EncodingHelpers.decodeInt(theAttributes.getValue(i)));
                builder.append(", time=");
                builder.append(theTime);
                builder.append("\n");
            } catch (ArrayIndexOutOfBounds aioob) {
                // won't happen
            }
        }

        log(builder.toString());
    }

    @Override
    public void discoverObjectInstance(int theObject, int theObjectClass, String objectName) throws CouldNotDiscover, ObjectClassNotKnown, FederateInternalError {
        System.out.println("Pojawil sie nowy obiekt typu SimObject");
    }
}
