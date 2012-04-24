package org.ethnochat.project;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import org.ethnochat.util.MutableNamedObject;

public class QuestionGroup extends AbstractList<Question>
        implements MutableNamedObject {

    private String name;
    private ArrayList<Question> questions;

    public static QuestionGroup unmodifiableQuestionGroup(QuestionGroup src) {
        return new QuestionGroup.ReadOnlyQuestionGroup(src);
    }

    public QuestionGroup() {
        name = "";
        questions = new ArrayList<Question>();
    }

    public QuestionGroup(String name) {
        this.name = name;
        questions = new ArrayList<Question>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override public Question get(int index) {
        return questions.get(index);
    }

    @Override public int size() {
        return questions.size();
    }

    @Override public boolean add(Question q) {
        if (questions.contains(q)) {
            return false;
        }
        return questions.add(q);
    }

    @Override public void add(int index, Question q) {
        if (!questions.contains(q)) {
            questions.add(index, q);
        }
    }

    @Override public Question remove(int index) {
        return questions.remove(index);
    }

    public boolean remove(Question q) {
        return questions.remove(q);
    }

    @Override public String toString() {
        return name;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof QuestionGroup)) {
            return false;
        }
        QuestionGroup g = (QuestionGroup)o;
        if (!name.equals(g.name)) {
            return false;
        }
        return questions.equals(g.questions);
    }

    @Override public int hashCode() {
        int hashCode = 13;
        hashCode = 31 * hashCode + questions.hashCode();
        hashCode = 31 * hashCode + name.hashCode();
        return hashCode;
    }

    protected ArrayList<Question> getQuestions() {
        return questions;
    }

    protected void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    private static class ReadOnlyQuestionGroup extends QuestionGroup {

        ReadOnlyQuestionGroup(QuestionGroup g) {
            super(g.getName());
            setQuestions(
                new ArrayList<Question>(
                    Collections.unmodifiableList(g.getQuestions())));
        }

        @Override public void setName(String name) {
            throw new UnsupportedOperationException();
        }

        @Override public boolean add(Question q) {
            throw new UnsupportedOperationException();
        }

        @Override public Question remove(int index) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Question q) {
            throw new UnsupportedOperationException();
        }
    }
}
