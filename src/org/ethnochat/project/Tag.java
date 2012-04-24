package org.ethnochat.project;

public class Tag implements Comparable<Tag> {

    private Integer id;
    private String text;

    public Tag() {
        this.id = -1;
        this.text = "";
    }

    public Tag(String text) {
        this.id = -1;
        this.text = text;
    }

    Tag(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public Integer getID() {
        return id;
    }

    public String getText() {
        return text;
    }

    @Override public String toString() {
        return text;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Tag)) {
            return false;
        }
        Tag t = (Tag)o;
        return (id.equals(t.id)
            && text.equals(t.text));
    }

    @Override public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + id.hashCode();
        hashCode = 31 * hashCode + text.hashCode();
        return hashCode;
    }

    public int compareTo(Tag t) {
        if (id.equals(t.id)) {
            return text.compareTo(t.text);
        } else {
            return id.compareTo(t.id);
        }
    }
}
