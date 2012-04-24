package org.ethnochat.ui;

interface ComplexListCellRenderer {

    public java.awt.Component getCellRendererComponent(
            ComplexList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus);

    public void setSelected(
        ComplexList list,
        java.awt.Component cell,
        boolean isSelected);

    public void setEditing(
        ComplexList list,
        java.awt.Component cell,
        boolean isEditing);
}
