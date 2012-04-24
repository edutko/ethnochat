package org.ethnochat.project.event;

import org.ethnochat.project.Question;
import org.ethnochat.project.QuestionGroup;

public class QuestionManagerEvent extends java.util.EventObject {

	private static final long serialVersionUID = 1815797749586943906L;

	private QuestionGroup group;
    private Question question;

    public QuestionManagerEvent(Object source,
            QuestionGroup group,
            Question question) {
        super(source);
        this.group = group;
        this.question = question;
    }

    public QuestionGroup getQuestionGroup() {
        return group;
    }

    public Question getQuestion() {
        return question;
    }
}
