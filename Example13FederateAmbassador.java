/*
 *   Copyright 2007 The Portico Project
 *
 *   This file is part of portico.
 *
 *   portico is free software; you can redistribute it and/or modify
 *   it under the terms of the Common Developer and Distribution License (CDDL)
 *   as published by Sun Microsystems. For more information see the LICENSE file.
 *
 *   Use of this software is strictly AT YOUR OWN RISK!!!
 *   If something bad happens you do not have permission to come crying to me.
 *   (that goes for your lawyer as well)
 *
 */
package hla13;

import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;

/**
 * This class handles all incoming callbacks from the RTI regarding a particular
 * {@link Example13Federate}. It will log information about any callbacks it
 * receives, thus demonstrating how to deal with the provided callback information.
 */
public class Example13FederateAmbassador extends Ambassador {
    public Example13FederateAmbassador() {
    }

    /**
     * As all time-related code is Portico-specific, we have isolated it into a single
     * method. This way, if you need to move to a different RTI, you only need to
     * change this code, rather than more code throughout the whole class.
     */

    public void discoverObjectInstance(int theObject, int theObjectClass, String objectName) {
        log("Discoverd Object: handle=" + theObject + ", classHandle=" + theObjectClass + ", name=" + objectName);
    }

    public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] tag) {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
        reflectAttributeValues(theObject, theAttributes, tag, null, null);
    }

    public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] tag, LogicalTime theTime, EventRetractionHandle retractionHandle) {
        StringBuilder builder = new StringBuilder("Reflection for object:");
        builder.append(" handle=").append(theObject);
        builder.append(", tag=").append(EncodingHelpers.decodeString(tag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if (theTime != null) {
            builder.append(", time=").append(convertTime(theTime));
        }

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
                builder.append(EncodingHelpers.decodeString(theAttributes.getValue(i)));
                builder.append("\n");
            } catch (ArrayIndexOutOfBounds aioob) {
                // won't happen
            }
        }

        log(builder.toString());
    }

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag) {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
        receiveInteraction(interactionClass, theInteraction, tag, null, null);
    }

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
        StringBuilder builder = new StringBuilder("Interaction Received:");
        // print the handle
        builder.append(" handle=").append(interactionClass);
        // print the tag
        builder.append(", tag=").append(EncodingHelpers.decodeString(tag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if (theTime != null) {
            builder.append(", time=").append(convertTime(theTime));
        }
        // print the parameer information
        builder.append(", parameterCount=").append(theInteraction.size());
        builder.append("\n");
        for (int i = 0; i < theInteraction.size(); i++) {
            try {
                // print the parameter handle
                builder.append("\tparamHandle=");
                builder.append(theInteraction.getParameterHandle(i));
                // print the parameter value
                builder.append(", paramValue=");
                builder.append(
                        EncodingHelpers.decodeString(theInteraction.getValue(i)));
                builder.append("\n");
            } catch (ArrayIndexOutOfBounds aioob) {
                // won't happen
            }
        }
        log(builder.toString());
    }

    public void removeObjectInstance(int theObject, byte[] userSuppliedTag) {
        log("Object Removed: handle=" + theObject);
    }

    public void removeObjectInstance(int theObject, byte[] userSuppliedTag, LogicalTime theTime, EventRetractionHandle retractionHandle) {
        log("Object Removed: handle=" + theObject);
    }
}
