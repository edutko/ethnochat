package org.ethnochat.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ethnochat.project.event.QuestionManagerEvent;
import org.ethnochat.project.event.QuestionManagerListener;

public class QuestionManager {

    private ArrayList<QuestionGroup> questionGroups;
    private LinkedList<QuestionManagerListener> questionManagerListeners;

    public QuestionManager() {
        questionGroups = new ArrayList<QuestionGroup>();
        questionManagerListeners = new LinkedList<QuestionManagerListener>();
    }

    public void addQuestionManagerListener(QuestionManagerListener l) {
        questionManagerListeners.add(l);
    }

    public boolean addQuestionGroup(QuestionGroup group) {
        boolean added = false;
        if (!questionGroups.contains(group)) {
            added = questionGroups.add(group);
            fireQuestionGroupAdded(group);
        }
        return added;
    }

    public void addQuestionGroup(QuestionGroup group, int index) {
        if (!questionGroups.contains(group)) {
            questionGroups.add(index, group);
            fireQuestionGroupAdded(group);
        }
    }

    public boolean addQuestion(QuestionGroup group, Question question) {
        boolean added = false;
        if (questionGroups.contains(group)
                && !group.contains(question)) {
            QuestionGroup g =
                questionGroups.get(questionGroups.indexOf(group));
            added = g.add(question);
            if (added) {
                fireQuestionAdded(g, question);
            }
        }
        return added;
    }

    public void addQuestion(QuestionGroup group, Question question, int index) {
        if (questionGroups.contains(group)
                && !group.contains(question)) {
            QuestionGroup g =
                questionGroups.get(questionGroups.indexOf(group));
            g.add(index, question);
            fireQuestionAdded(g, question);
        }
    }

    public void changeQuestion(
            QuestionGroup group,
            Question oldQuestion,
            Question newQuestion) {
        if (questionGroups.contains(group) && group.contains(oldQuestion)) {
            QuestionGroup g =
                questionGroups.get(questionGroups.indexOf(group));
            int index = g.indexOf(oldQuestion);
            g.remove(index);
            g.add(index, newQuestion);
            fireQuestionChanged(group, newQuestion);
        }
    }

    public void changeQuestionGroupName(QuestionGroup group, String newName) {
        if (questionGroups.contains(group)) {
            group.setName(newName);
            fireQuestionGroupChanged(group);
        }
    }

    public QuestionGroup getGroupForQuestion(Question question) {
        QuestionGroup group = null;
        for (QuestionGroup g : questionGroups) {
            if (g.contains(question)) {
                group = QuestionGroup.unmodifiableQuestionGroup(g);
                break;
            }
        }
        return group;
    }

    public List<QuestionGroup> getQuestionGroups() {
        return Collections.unmodifiableList(questionGroups);
    }

    public QuestionGroup getQuestionGroup(int index) {
        return QuestionGroup.unmodifiableQuestionGroup(
            questionGroups.get(index));
    }

    public int getQuestionGroupCount() {
        return questionGroups.size();
    }

    public int getQuestionCount() {
        int count = 0;
        for (QuestionGroup group : questionGroups) {
            count += group.size();
        }
        return count;
    }

    public int indexOfQuestionGroup(QuestionGroup group) {
        return questionGroups.indexOf(group);
    }

    public int indexOfQuestion(QuestionGroup group, Question question) {
        if (!questionGroups.contains(group)) {
            return -1;
        }
        return group.indexOf(question);
    }

    public void removeQuestionGroup(QuestionGroup group) {
        if (questionGroups.contains(group)) {
            fireQuestionGroupRemoved(group);
            questionGroups.remove(group);
        }
    }

    public void removeQuestion(QuestionGroup group, Question question) {
        if (questionGroups.contains(group)
                && group.contains(question)) {
            QuestionGroup g = questionGroups.get(questionGroups.indexOf(group));
            fireQuestionRemoved(g, question);
            g.remove(question);
        }
    }

    public void removeQuestionManagerListener(QuestionManagerListener l) {
        questionManagerListeners.remove(l);
    }

    private void fireQuestionAdded(QuestionGroup g, Question q) {
        QuestionManagerEvent event = new QuestionManagerEvent(this, g, q);
        for (QuestionManagerListener l : questionManagerListeners) {
            l.questionAdded(event);
        }
    }

    private void fireQuestionChanged(QuestionGroup g, Question q) {
        QuestionManagerEvent event = new QuestionManagerEvent(this, g, q);
        for (QuestionManagerListener l : questionManagerListeners) {
            l.questionChanged(event);
        }
    }

    private void fireQuestionRemoved(QuestionGroup g, Question q) {
        QuestionManagerEvent event = new QuestionManagerEvent(this, g, q);
        for (QuestionManagerListener l : questionManagerListeners) {
            l.questionRemoved(event);
        }
    }

    private void fireQuestionGroupAdded(QuestionGroup g) {
        QuestionManagerEvent event = new QuestionManagerEvent(this, g, null);
        for (QuestionManagerListener l : questionManagerListeners) {
            l.questionGroupAdded(event);
        }
    }

    private void fireQuestionGroupChanged(QuestionGroup g) {
        QuestionManagerEvent event = new QuestionManagerEvent(this, g, null);
        for (QuestionManagerListener l : questionManagerListeners) {
            l.questionGroupChanged(event);
        }
    }

    private void fireQuestionGroupRemoved(QuestionGroup g) {
        QuestionManagerEvent event = new QuestionManagerEvent(this, g, null);
        for (QuestionManagerListener l : questionManagerListeners) {
            l.questionGroupRemoved(event);
        }
    }
}
