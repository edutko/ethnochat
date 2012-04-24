package org.ethnochat.ui;

import org.ethnochat.project.TagManager;

class TagManagerComboBoxModelAdapter extends TagManagerListModelAdapter
        implements javax.swing.ComboBoxModel {

	private static final long serialVersionUID = 5956942703998232389L;

	private Object selectedItem;

    public TagManagerComboBoxModelAdapter(TagManager tagMgr) {
        super(tagMgr);
    }

    public Object getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Object o) {
        selectedItem = o;
    }
}
