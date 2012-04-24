package org.ethnochat.messaging;

public class Message implements Comparable<Message> {

    private java.util.Date timestamp;
    private Contact from;
    private String text;

    public Message(Contact from, String text) {
        this(new java.util.Date(), from, text);
    }

    public Message(Message msg) {
        this.timestamp = msg.timestamp;
        this.from = msg.from;
        this.text = msg.text;
    }

    public Message(java.util.Date timestamp, Contact from, String text) {
        this.timestamp = timestamp;
        this.from = from;
        this.text = text;
    }

    public Contact getContact() {
        return from;
    }

    public String getText() {
        return text;
    }

    public java.util.Date getTimestamp() {
        return timestamp;
    }

    @Override public String toString() {
        return "[" + timestamp + "] " + from + ": " + text;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Message)) {
            return false;
        }
        Message m = (Message)o;
        return (timestamp.equals(m.timestamp)
            && from.equals(m.from)
            && text.equals(m.text));
    }

    @Override public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + timestamp.hashCode();
        hashCode = 31 * hashCode + from.hashCode();
        hashCode = 31 * hashCode + text.hashCode();
        return hashCode;
    }

    public int compareTo(Message m) {
        if (!timestamp.equals(m.timestamp)) {
            return timestamp.compareTo(m.timestamp);
        } else if (!from.equals(m.from)) {
            return from.compareTo(m.from);
        } else {
            return text.compareTo(m.text);
        }
    }
}
