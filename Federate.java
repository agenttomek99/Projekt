package hla13;

import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

import javax.management.InvalidAttributeValueException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public abstract class Federate {
    public static final String READY_TO_RUN = "ReadyToRun";
    protected RTIambassador rtiAmb;
    protected Ambassador fedAmb;
    protected final double timeStep = 10.0;
    protected String fedFileName = "store.fed";

    public void runFederate() throws RTIexception, InvalidAttributeValueException {
        rtiAmb = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();

        fedAmb = this.createAmbassador();
        String className = this.getName();
        rtiAmb.joinFederationExecution(className, "ExampleFederation", fedAmb);
        log("Joined Federation as " + className);

//        rtiAmb.registerFederationSynchronizationPoint(READY_TO_RUN, null);

//        while (!fedAmb.isAnnounced) {
//            rtiAmb.tick();
//        }
//
//        rtiAmb.synchronizationPointAchieved(READY_TO_RUN);
//        log("Achieved sync point: " + READY_TO_RUN + ", waiting for federation...");
//        while (!fedAmb.isReadyToRun) {
//            rtiAmb.tick();
//        }

        enableTimePolicy();
        publishAndSubscribe();
        initialize();
        log("Published and Subscribed");

        while (fedAmb.running) {
            this.tick();
            rtiAmb.tick();
        }
    }

    protected String getName() {
        return this.getClass().getSimpleName();
    }

    protected void sendInteraction(String name, int id, int otherId) throws InvalidAttributeValueException, RTIinternalError, FederateNotExecutionMember, InteractionParameterNotDefined, RestoreInProgress, InteractionClassNotDefined, ConcurrentAccessAttempted, InteractionClassNotPublished, SaveInProgress, InvalidFederationTime, NameNotFound {
        sendInteraction(name, new int[]{id, otherId});
    }

    protected void sendInteraction(String name, int id) throws InvalidAttributeValueException, RTIinternalError, FederateNotExecutionMember, InteractionParameterNotDefined, RestoreInProgress, InteractionClassNotDefined, ConcurrentAccessAttempted, InteractionClassNotPublished, SaveInProgress, InvalidFederationTime, NameNotFound {
        sendInteraction(name, new int[]{id});
    }

    protected void sendInteraction(String name, int[] attributes) throws InvalidAttributeValueException, RTIinternalError, FederateNotExecutionMember, InteractionParameterNotDefined, RestoreInProgress, InteractionClassNotDefined, ConcurrentAccessAttempted, InteractionClassNotPublished, SaveInProgress, InvalidFederationTime, NameNotFound {
        int interactionHandle = rtiAmb.getInteractionClassHandle("InteractionRoot." + name);
        System.out.println(getName() + ": Sending Interaction: " + name + " " + Arrays.toString(attributes));
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        for (int i = 0; i < attributes.length; i++) {
            if (attributes[i] == -1) {
                throw new InvalidAttributeValueException(name);
            }
            byte[] attrEncoded = EncodingHelpers.encodeInt(attributes[i]);
            int attrHandle = rtiAmb.getParameterHandle(String.valueOf(i), interactionHandle);
            parameters.add(attrHandle, attrEncoded);
        }

        LogicalTime time = convertTime(fedAmb.federateTime + timeStep);
        rtiAmb.sendInteraction(interactionHandle, parameters, "tag".getBytes(), time);
    }

    protected Ambassador createAmbassador() {
        return new Ambassador();
    }

    protected void initialize() throws RTIexception, InvalidAttributeValueException {
    }

    protected abstract void tick() throws RTIexception, InvalidAttributeValueException;

    protected abstract void publishAndSubscribe() throws RTIexception;

    protected void publishAndSubscribe(String[] toPublish, String[] toSubscribe) throws RTIexception {
        fedAmb.interactionHandleMap = new HashMap<>();
        for (String interactionName : toPublish) {
            int interactionHandle = rtiAmb.getInteractionClassHandle("InteractionRoot." + interactionName);
//            fedAmb.interactionHandleMap.put(interactionHandle, interactionName);
            rtiAmb.publishInteractionClass(interactionHandle);
        }

        for (String interactionName : toSubscribe) {
            int interactionHandle = rtiAmb.getInteractionClassHandle("InteractionRoot." + interactionName);
            fedAmb.interactionHandleMap.put(interactionHandle, interactionName);
            rtiAmb.subscribeInteractionClass(interactionHandle);
        }
    }

    protected void enableTimePolicy() throws RTIexception {
        // NOTE: Unfortunately, the LogicalTime/LogicalTimeInterval create code is
        //       Portico specific. You will have to alter this if you move to a
        //       different RTI implementation. As such, we've isolated it into a
        //       method so that any change only needs to happen in a couple of spots
        LogicalTime currentTime = convertTime(fedAmb.federateTime);
        LogicalTimeInterval lookahead = convertInterval(fedAmb.federateLookahead);

        this.rtiAmb.enableTimeRegulation(currentTime, lookahead);

        while (!fedAmb.isRegulating) {
            rtiAmb.tick();
        }

        this.rtiAmb.enableTimeConstrained();

        while (!fedAmb.isConstrained) {
            rtiAmb.tick();
        }
    }

    protected void advanceTime(double timeStep) throws RTIexception {
//        log("requesting time advance for: " + timeStep);
//        log("requesting time advance for newtime: " + (fedAmb.federateTime + timeStep));
//        // request the advance
        fedAmb.isAdvancing = true;
        LogicalTime newTime = convertTime(fedAmb.federateTime + timeStep);
        rtiAmb.timeAdvanceRequest(newTime);
        while (fedAmb.isAdvancing) {
            rtiAmb.tick();
        }
    }

    protected void destroyFederation() throws FederateNotExecutionMember, InvalidResignAction, ConcurrentAccessAttempted, FederateOwnsAttributes, RTIinternalError {
        rtiAmb.resignFederationExecution(ResignAction.NO_ACTION);
        log("Resigned from Federation");
        try {
            rtiAmb.destroyFederationExecution("ExampleFederation");
            log("Destroyed Federation");
        } catch (FederationExecutionDoesNotExist dne) {
            log("No need to destroy federation, it doesn't exist");
        } catch (FederatesCurrentlyJoined fcj) {
            log("Didn't destroy federation, federates still joined");
        }
    }

    protected double randomTime() {
        return 1 + (9 * new Random().nextDouble());
    }

    protected LogicalTime convertTime(double time) {
        return new DoubleTime(time);
    }

    protected LogicalTimeInterval convertInterval(double time) {
        return new DoubleTimeInterval(time);
    }

    protected void log(String message) {
        System.out.println(this.getClass().getSimpleName() + " : " + message);
    }
}
