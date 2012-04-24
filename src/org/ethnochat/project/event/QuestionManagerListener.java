package org.ethnochat.project.event;

public interface QuestionManagerListener {

    public void questionAdded(QuestionManagerEvent event);
    public void questionChanged(QuestionManagerEvent event);
    public void questionRemoved(QuestionManagerEvent event);
    public void questionGroupAdded(QuestionManagerEvent event);
    public void questionGroupChanged(QuestionManagerEvent event);
    public void questionGroupRemoved(QuestionManagerEvent event);
}
