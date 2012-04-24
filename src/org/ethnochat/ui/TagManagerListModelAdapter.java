package org.ethnochat.ui;

import java.util.ArrayList;
import java.util.Collections;

import org.ethnochat.project.Tag;
import org.ethnochat.project.TagManager;
import org.ethnochat.project.event.TagManagerEvent;
import org.ethnochat.project.event.TagManagerListener;

class TagManagerListModelAdapter extends javax.swing.AbstractListModel
        implements TagManagerListener {

	private static final long serialVersionUID = -8045513692015022913L;

	private TagManager tagMgr;
    private ArrayList<Tag> sortedTagList;

    public TagManagerListModelAdapter(TagManager tagMgr) {
        this.tagMgr = tagMgr;
        sortedTagList = new ArrayList<Tag>(tagMgr.getTags());
        Collections.sort(sortedTagList, new TagSorter());
        tagMgr.addTagManagerListener(this);
    }

    public Object get(int index) {
        return getElementAt(index);
    }

    public Object getElementAt(int index) {
        if (index < 0 || index >= getSize()) {
            return null;
        }
        return sortedTagList.get(index);
    }

    public int getSize() {
        return tagMgr.getTagCount();
    }

    public void addElement(Object o) {
        if (o instanceof Tag) {
            Tag tag = (Tag)o;
            if (!sortedTagList.contains(tag)) {
                tagMgr.addTag(tag);
            }
        } else {
            throw new RuntimeException(
                "Invalid object type passed to "
                + "TagManagerListModelAdapter.addElement()");
        }
    }

    public void changeElementText(Object o, String text) {
        if (o instanceof Tag) {
            Tag oldTag = (Tag)o;
            Tag newTag = new Tag(text);
            tagMgr.changeTag(oldTag, newTag);
        } else {
            throw new RuntimeException(
                "Invalid object type passed to "
                + "TagManagerListModelAdapter.changeElementText()");
        }
    }

    public void remove(int index) {
        Tag tag = (Tag)getElementAt(index);
        tagMgr.removeTag(tag);
    }

    public void tagAdded(TagManagerEvent event) {
        Tag tag = event.getTag();
        int index = findInsertionIndex(tag);
        sortedTagList.add(index, tag);
        fireIntervalAdded(this, index, index);
    }

    public void tagChanged(TagManagerEvent event) {
        Tag newTag = event.getTag();
        Tag oldTag = sortedTagList.get(0);
        int oldTagIndex = 0;
        while (oldTagIndex < sortedTagList.size()) {
            oldTag = sortedTagList.get(oldTagIndex);
            if (oldTag.getID().equals(newTag.getID())) {
                break;
            }
            oldTagIndex++;
        }

        if (oldTagIndex == sortedTagList.size()) {
            throw new RuntimeException(
                "Attempted to change a tag (" + newTag.toString()
                + ") that does not exist.");
        }

        sortedTagList.remove(oldTag);
        int newTagIndex = findInsertionIndex(newTag);
        sortedTagList.add(newTagIndex, newTag);

        fireContentsChanged(
            this,
            Math.min(oldTagIndex, newTagIndex),
            Math.max(oldTagIndex, newTagIndex));
    }

    public void tagRemoved(TagManagerEvent event) {
        Tag tag = event.getTag();
        int indexOfTag = indexOf(tag);
        sortedTagList.remove(tag);
        fireIntervalRemoved(this, indexOfTag, indexOfTag);
    }

    private int findInsertionIndex(Tag tag) {
        int index =  Collections.binarySearch(sortedTagList, tag, new TagSorter());
        if (index < 0) {
            index = (index + 1) * -1;
        }
        return index;
    }

    private int indexOf(Object o) {
        return sortedTagList.indexOf(o);
    }

    private static class TagSorter implements java.util.Comparator<Tag> {
        public int compare(Tag tag1, Tag tag2) {
            // A blank tag should always end up at the end of the list.
            // There should never be more than one blank tag in the list, but
            // if there are, they will all end up at the end.
            if (tag1.getText().equals("")
                    && !tag2.getText().equals("")) {
                return 1;
            } else if (tag2.getText().equals("")
                    && !tag1.getText().equals("")) {
                return -1;
            } else {
                return tag1.getText().compareTo(tag2.getText());
            }
        }
    }
}
