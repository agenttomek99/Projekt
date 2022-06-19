package hla13.producerConsumer;


import java.util.Comparator;

public class ExternalEvent {

    public enum EventType {ADD, GET}

    private  int qty;
    private EventType eventType;
    private Double time;

    public ExternalEvent(int qty, EventType eventType, Double time) {
        this.qty = qty;
        this.eventType = eventType;
        this.time = time;

    }

    public EventType getEventType() {
        return eventType;
    }

    public int getQty() {
        return qty;
    }

    public double getTime() {
        return time;
    }

    static class ExternalEventComparator implements Comparator<ExternalEvent> {

        @Override
        public int compare(ExternalEvent o1, ExternalEvent o2) {
            return o1.time.compareTo(o2.time);
        }
    }
}
