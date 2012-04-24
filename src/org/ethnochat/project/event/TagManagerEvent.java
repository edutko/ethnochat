package org.ethnochat.project.event;

import org.ethnochat.project.Tag;

public class TagManagerEvent extends java.util.EventObject {

	private static final long serialVersionUID = 6376151426352165710L;

	private Tag tag;

    public TagManagerEvent(Object source, Tag tag) {
        super(source);
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }
}
