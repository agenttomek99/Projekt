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
import hla.rti.jlc.RtiFactoryFactory;

import javax.management.InvalidAttributeValueException;

/**
 * This is an example federate demonstrating how to properly use the HLA 1.3 Java
 * interface supplied with Portico.
 * <p>
 * As it is intended for example purposes, this is a rather simple federate. The
 * process is goes through is as follows:
 * <p>
 * 1. Create the RTIambassador
 * 2. Try to create the federation (nofail)
 * 3. Join the federation
 * 4. Announce a Synchronization Point (nofail)
 * 5. Wait for the federation to Synchronized on the point
 * 6. Enable Time Regulation and Constrained
 * 7. Publish and Subscribe
 * 8. Register an Object Instance
 * 9. Main Simulation Loop (executes 20 times)
 * 9.1 Update attributes of registered object
 * 9.2 Send an Interaction
 * 9.3 Advance time by 1.0
 * 10. Delete the Object Instance
 * 11. Resign from Federation
 * 12. Try to destroy the federation (nofail)
 * <p>
 * NOTE: Those items marked with (nofail) deal with situations where multiple
 * federates may be working in the federation. In this sitaution, the
 * federate will attempt to carry out the tasks defined, but it won't
 * stop or exit if they fail. For example, if another federate has already
 * created the federation, the call to create it again will result in an
 * exception. The example federate expects this and will not fail.
 * NOTE: Between actions 4. and 5., the federate will pause until the uses presses
 * the enter key. This will give other federates a chance to enter the
 * federation and prevent other federates from racing ahead.
 * <p>
 * <p>
 * The main method to take notice of is runFederate(). It controls the
 * main simulation loop and triggers most of the important behaviour. To make the code
 * simpler to read and navigate, many of the important HLA activities are broken down
 * into separate methods. For example, if you want to know how to send an interaction,
 * see the {@link #sendInteraction()} method.
 * <p>
 * With regard to the FederateAmbassador, it will log all incoming information. Thus,
 * if it receives any reflects or interactions etc... you will be notified of them.
 * <p>
 * Note that all of the methods throw an RTIexception. This class is the parent of all
 * HLA exceptions. The HLA Java interface is full of exceptions, with only a handful
 * being actually useful. To make matters worse, they're all checked exceptions, so
 * unlike C++, we are forced to handle them by the compiler. This is unnecessary in
 * this small example, so we'll just throw all exceptions out to the main method and
 * handle them there, rather than handling each exception independently as they arise.
 */
public class Example13Federate extends Federate {
    /*** The number of times we will update our attributes and send an interaction*/
    public static final int ITERATIONS = 20;
    /*** The sync point all federates will sync up on before starting*/
    int objectHandle;

    @Override
    protected Example13FederateAmbassador createAmbassador() {
        return new Example13FederateAmbassador();
    }

    @Override
    protected void tick() throws RTIexception {
        updateAttributeValues(objectHandle);
        sendInteraction();
        advanceTime(1.0);
        log("Time Advanced to " + fedAmb.federateTime);
    }

    @Override
    protected void initialize() throws RTIexception {
        objectHandle = registerObject();
    }

    /**
     * This method will inform the RTI about the types of data that the federate will
     * be creating, and the types of data we are interested in hearing about as other
     * federates produce it.
     */
    protected void publishAndSubscribe() throws RTIexception {
        // publish all attributes of ObjectRoot.A before we can register
        // instance of the object class ObjectRoot.A and
        // update the values of the various attributes, we need to tell the RTI
        // that we intend to publish this information

        int classHandle = rtiAmb.getObjectClassHandle("ObjectRoot.A");
        AttributeHandleSet attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
        for (String attr : new String[]{"aa", "ab", "ac"}) {
            int atrHandle = rtiAmb.getAttributeHandle(attr, classHandle);
            attributes.add(atrHandle);
        }

        rtiAmb.publishObjectClass(classHandle, attributes);
        // subscribe to all attributes of ObjectRoot.A //
        // we also want to hear about the same sort of information as it is
        // created and altered in other federates, so we need to subscribe to it
        rtiAmb.subscribeObjectClassAttributes(classHandle, attributes);

        // publish the interaction class InteractionRoot.X //
        // we want to send interactions of type InteractionRoot.X, so we need
        // to tell the RTI that we're publishing it first. We don't need to
        // inform it of the parameters, only the class, making it much simpler
        int interactionHandle = rtiAmb.getInteractionClassHandle("InteractionRoot.X");

        // do the publication
        rtiAmb.publishInteractionClass(interactionHandle);

        // subscribe to the InteractionRoot.X interaction //
        // we also want to receive other interaction of the same type that are
        // sent out by other federates, so we have to subscribe to it first
        rtiAmb.subscribeInteractionClass(interactionHandle);
    }

    /**
     * This method will register an instance of the class ObjectRoot.A and will
     * return the federation-wide unique handle for that instance. Later in the
     * simulation, we will update the attribute values for this instance
     */
    private int registerObject() throws RTIexception {
        System.out.println("regeestehing");
        int classHandle = rtiAmb.getObjectClassHandle("ObjectRoot.A");
        return rtiAmb.registerObjectInstance(classHandle);
    }

    /**
     * This method will update all the values of the given object instance. It will
     * set each of the values to be a string which is equal to the name of the
     * attribute plus the current time. eg "aa:10.0" if the time is 10.0.
     * <p/>
     * Note that we don't actually have to update all the attributes at once, we
     * could update them individually, in groups or not at all!
     */
    private void updateAttributeValues(int objectHandle) throws RTIexception {
        SuppliedAttributes attributes = RtiFactoryFactory.getRtiFactory().createSuppliedAttributes();
        // this line gets the object class of the instance identified by the
        // object instance the handle points to
        int classHandle = rtiAmb.getObjectClass(objectHandle);

        for (String attr : new String[]{"aa", "ab", "ac"}) {
            // we use EncodingHelpers to make things nice friendly for both Java and C++
            byte[] value = EncodingHelpers.encodeString(attr + ":" + getLbts());
            int handle = rtiAmb.getAttributeHandle(attr, classHandle);
            attributes.add(handle, value);
        }

        LogicalTime time = convertTime(fedAmb.federateTime + fedAmb.federateLookahead);
        rtiAmb.updateAttributeValues(objectHandle, attributes, generateTag(), time);
    }

    /**
     * This method will send out an interaction of the type InteractionRoot.X. Any
     * federates which are subscribed to it will receive a notification the next time
     * they tick(). Here we are passing only two of the three parameters we could be
     * passing, but we don't actually have to pass any at all!
     */
    private void sendInteraction() throws RTIexception {
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();
        int classHandle = rtiAmb.getInteractionClassHandle("InteractionRoot.X");
        for (String attr : new String[]{"xa", "xb"}) {
            // we use EncodingHelpers to make things nice friendly for both Java and C++
            byte[] attrValue = EncodingHelpers.encodeString(attr + ":" + getLbts());
            int attrHandle = rtiAmb.getParameterHandle(attr, classHandle);
            parameters.add(attrHandle, attrValue);
        }

        LogicalTime time = convertTime(fedAmb.federateTime + fedAmb.federateLookahead);
        rtiAmb.sendInteraction(classHandle, parameters, generateTag(), time);
    }

    /**
     * We can only delete objects we created, or for which we own the
     * privilegeToDelete attribute.
     */
    private void deleteObject(int handle) throws RTIexception {
        rtiAmb.deleteObjectInstance(handle, generateTag());
    }

    private double getLbts() {
        return fedAmb.federateTime + fedAmb.federateLookahead;
    }

    private byte[] generateTag() {
        return ("" + System.currentTimeMillis()).getBytes();
    }

    public static void main(String[] args) throws InvalidAttributeValueException {
        try {
            new Example13Federate().runFederate();
        } catch (RTIexception rtie) {
            rtie.printStackTrace();
        }
    }
}