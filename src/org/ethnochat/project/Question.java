package org.ethnochat.project;

public class Question implements Comparable<Question> {

    private String text;

    public Question() {
        text = new String("");
     }

    public Question(String text) {
        this.text = text;
     }

    public String getText() {
        return text;
    }

    @Override public String toString() {
        return text;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Question)) {
            return false;
        }
        Question q = (Question)o;
        return text.equals(q.text);
    }

    @Override public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + text.hashCode();
        return hashCode;
    }

    public int compareTo(Question q) {
        return text.compareTo(q.text);
    }
}
