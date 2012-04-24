package org.ethnochat.project.event;

public interface ConversationManagerListener {

    public void conversationStarted(ConversationManagerEvent event);
    public void conversationEnded(ConversationManagerEvent event);
}
