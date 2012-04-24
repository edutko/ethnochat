package org.ethnochat.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.ActionMap;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class ComplexList extends ScrollableJPanel
        implements javax.swing.event.ListDataListener {

	private static final long serialVersionUID = -6039404582593329221L;

	private ListSelectionModel selectionModel;
    private ListSelectionListener selectionHandler;
    private ArrayList<ListSelectionListener> selectionListeners;
    private ListModel model;
    private ComplexListCellRenderer cellRenderer;
    private ActionMap cellActionMap;
    private Color selectionForeground;
    private Color selectionBackground;
    private int editingIndex;

    public ComplexList(ListModel model) {
        this(model, null);
    }

    public ComplexList(ListModel model, ActionMap cellActionMap) {
        this.model = model;
        this.cellActionMap = cellActionMap;

        selectionListeners = new ArrayList<ListSelectionListener>();

        model.addListDataListener(this);

        selectionModel = createSelectionModel();
        selectionHandler = new ListSelectionHandler();
        getSelectionModel().addListSelectionListener(selectionHandler);
        editingIndex = -1;

        cellRenderer = new DefaultComplexListCellRenderer();
        selectionForeground = UIManager.getColor("List.selectionForeground");
        selectionBackground = UIManager.getColor("List.selectionBackground");

        addMouseListener(new ComplexListCellMouseListener());

        layoutComponents();
    }

    public void addListSelectionListener(ListSelectionListener l)
    {
        selectionListeners.add(l);
    }

    public void removeListSelectionListener(ListSelectionListener l)
    {
        selectionListeners.remove(l);
    }

    public ActionMap getListCellActionMap() {
        return cellActionMap;
    }

    public ListModel getModel() {
        return model;
    }

    public Color getSelectionForeground() {
        return selectionForeground;
    }

    public Color getSelectionBackground() {
        return selectionBackground;
    }

    public void setListCellActionMap(ActionMap cellActionMap) {
        this.cellActionMap = cellActionMap;
    }

    public void setListCellRenderer(ComplexListCellRenderer cellRenderer) {
        this.cellRenderer = cellRenderer;
        removeAll();
        for (int i = 0; i < model.getSize(); i++) {
            Object o = model.getElementAt(i);
            addCell(o, i);
        }
        setSize(getPreferredSize());
        validate();
    }

    public Object getSelectedValue() {
        int selectedIndex = getMinSelectionIndex();
        if (selectedIndex != -1) {
            return model.getElementAt(selectedIndex);
        } else {
            return null;
        }
    }

    public int getMinSelectionIndex() {
        return getSelectionModel().getMinSelectionIndex();
    }

    public int getMaxSelectionIndex() {
        return getSelectionModel().getMaxSelectionIndex();
    }

    public int getSelectedIndex() {
        return getMinSelectionIndex();
    }

    public int indexOf(Component c) {
        return getComponentZOrder(c);
    }

    public void setEditingIndex(int index) {
        if (index != editingIndex) {
            if (editingIndex != -1) {
                // Some other cell was being edited.
                cellRenderer.setEditing(this, getComponent(editingIndex), false);
            }
            if (index != -1) {
                // Some cell is now being edited.
                cellRenderer.setEditing(this, getComponent(index), true);
            }
        }
        editingIndex = index;
    }

    public void setSelectedIndex(int index) {
        if (index != -1) {
            setSelectionInterval(index, index);
        }
    }

    public void setSelectionInterval(int anchor, int lead) {
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            if (i >= anchor && i <= lead) {
                cellRenderer.setSelected(this, getComponent(i), true);
            } else {
                cellRenderer.setSelected(this, getComponent(i), false);
            }
        }
        getSelectionModel().setSelectionInterval(anchor, lead);
    }

    public void contentsChanged(javax.swing.event.ListDataEvent event) {
        int index0 = event.getIndex0();
        int index1 = event.getIndex1();
        for (int i = index0; i <= index1; i++) {
            remove(i);
            Object o = model.getElementAt(i);
            addCell(o, index0);
        }
        validate();
    }

    public void intervalAdded(javax.swing.event.ListDataEvent event) {
        int index0 = event.getIndex0();
        int index1 = event.getIndex1();
        int length = index1 - index0 + 1;
        getSelectionModel().insertIndexInterval(index0, length, true);
        for (int i = index1; i >= index0; i--) {
            Object o = model.getElementAt(i);
            addCell(o, index0);
        }
        setSize(getPreferredSize());
        validate();
    }

    public void intervalRemoved(javax.swing.event.ListDataEvent event) {
        int index0 = event.getIndex0();
        int index1 = event.getIndex1();
        for (int i = index0; i <= index1; i++) {
            remove(index0);
        }
        getSelectionModel().removeIndexInterval(index0, index1);
        setSize(getPreferredSize());
        validate();
    }

    protected ListSelectionModel createSelectionModel() {
        return new DefaultListSelectionModel();
    }

    protected ListSelectionModel getSelectionModel() {
        return selectionModel;
    }

    protected void fireSelectionValueChanged(
            int firstIndex,
            int lastIndex,
            boolean isAdjusting) {
        ListSelectionEvent e =
            new ListSelectionEvent(this, firstIndex, lastIndex, isAdjusting);
        for (ListSelectionListener l : selectionListeners) {
            l.valueChanged(e);
        }
    }

    private void addCell(Object value, int index) {
        boolean isSelected = false;
        if (index == getSelectedIndex()) {
            isSelected = true;
        }
        boolean cellHasFocus = false;
        if (isSelected) {
            cellHasFocus = true;
        }
        Component c =
            cellRenderer.getCellRendererComponent(
                this, value, index, isSelected, cellHasFocus);
        c.addMouseListener(new ComplexListCellMouseListener());
        add(c, index);
    }

    private void layoutComponents() {
        setLayout(
            new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        for (int i = 0; i < model.getSize(); i++) {
            Object o = model.getElementAt(i);
            addCell(o, i);
        }
    }

    private class ComplexListCellMouseListener
            extends java.awt.event.MouseAdapter {

        @Override
		public void mouseClicked(java.awt.event.MouseEvent event) {
            Component c = event.getComponent();
            int clickIndex = getComponentZOrder(c);
            if (getSelectionModel().isSelectedIndex(clickIndex)) {
                setEditingIndex(clickIndex);
            } else {
                setEditingIndex(-1);
                setSelectedIndex(clickIndex);
            }
        }
    }

    private class ListSelectionHandler implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e) {
            fireSelectionValueChanged(
                e.getFirstIndex(),
                e.getLastIndex(),
                e.getValueIsAdjusting());
        }
    }
}
