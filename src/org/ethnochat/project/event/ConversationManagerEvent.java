package org.ethnochat.project.event;

import org.ethnochat.project.Conversation;

public class ConversationManagerEvent extends java.util.EventObject {

	private static final long serialVersionUID = 643833179594994144L;

	private Conversation conversation;

    public ConversationManagerEvent(Object source, Conversation conversation) {
        super(source);
        this.conversation = conversation;
    }

    public Conversation getConversation() {
        return conversation;
    }
}
