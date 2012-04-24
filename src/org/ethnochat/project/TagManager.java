package org.ethnochat.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import org.ethnochat.project.event.TagManagerEvent;
import org.ethnochat.project.event.TagManagerListener;

public class TagManager {

    private Integer nextTagID;
    private HashMap<Integer, Tag> tagMap;
    private ArrayList<Tag> tagList;
    private LinkedList<TagManagerListener> tagManagerListeners;

    public TagManager() {
        nextTagID = new Integer(1);
        tagMap = new HashMap<Integer, Tag>();
        tagList = new ArrayList<Tag>();
        tagManagerListeners = new LinkedList<TagManagerListener>();
    }

    public void addTagManagerListener(TagManagerListener l) {
        tagManagerListeners.add(l);
    }

    public void addTag(Tag tag) {
        addTag(tag, getTagCount());
    }

    public void addTag(Tag tag, int index) {
        Integer id = tag.getID();
        if (id.intValue() == -1) {
            id = getNextTagID();
            tag = new Tag(id, tag.getText());
        }
        if (getTagByID(id) != null) {
            throw new IllegalArgumentException("Attempted to add a tag ("
                + tag.toString() + ") with an ID that is already in use.");
        }
        tagMap.put(id, tag);
        tagList.add(index, tag);
        fireTagAdded(tag);
    }

    public void changeTag(Tag oldTag, Tag newTag) {
        if (!tagList.contains(oldTag)) {
            throw new IllegalArgumentException("Attempted to change a tag ("
                + oldTag.toString() + ") that is unknown to the tag manager.");
        }
        Integer id = oldTag.getID();
        Tag newTagWithOldID = new Tag(id, newTag.getText());
        tagMap.remove(id);
        tagMap.put(id, newTagWithOldID);
        tagList.set(tagList.indexOf(oldTag), newTagWithOldID);
        fireTagChanged(newTagWithOldID);
    }

    public int getTagCount() {
        return tagList.size();
    }

    public Tag getTag(int index) {
        return tagList.get(index);
    }

    public Tag getTagByID(Integer id) {
        return tagMap.get(id);
    }

    public List<Tag> getTags() {
        return Collections.unmodifiableList(tagList);
    }

    public void removeTag(Tag tag) {
        if (tagList.contains(tag)) {
            fireTagRemoved(tag);
            tagMap.remove(tag.getID());
            tagList.remove(tag);
        }
    }

    public void removeTagManagerListener(TagManagerListener l) {
        tagManagerListeners.remove(l);
    }

    private synchronized Integer getNextTagID() {
        return nextTagID++;
    }

    private void fireTagAdded(Tag t) {
        TagManagerEvent event = new TagManagerEvent(this, t);
        for (TagManagerListener l : tagManagerListeners) {
            l.tagAdded(event);
        }
    }

    private void fireTagChanged(Tag t) {
        TagManagerEvent event = new TagManagerEvent(this, t);
        for (TagManagerListener l : tagManagerListeners) {
            l.tagChanged(event);
        }
    }

    private void fireTagRemoved(Tag t) {
        TagManagerEvent event = new TagManagerEvent(this, t);
        for (TagManagerListener l : tagManagerListeners) {
            l.tagRemoved(event);
        }
    }
}
