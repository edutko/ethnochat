package org.ethnochat.ui;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;

class DefaultComplexListCellRenderer implements ComplexListCellRenderer {

    public Component getCellRendererComponent(
            ComplexList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        JPanel p = new JPanel();

        GridBagLayout gb = new GridBagLayout();
        p.setLayout(gb);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weighty = 0.0;
        c.weightx = 1.0;

        javax.swing.JTextArea text = new javax.swing.JTextArea();
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.append(value.toString());
        gb.setConstraints(text, c);
        p.add(text);

        return p;
    }

    public void setSelected(
            ComplexList list,
            Component cell,
            boolean isSelected) {
        if (isSelected) {
            cell.setBackground(list.getSelectionBackground());
            cell.setForeground(list.getSelectionForeground());
        } else {
            cell.setBackground(list.getBackground());
            cell.setForeground(list.getForeground());
        }
    }

    public void setEditing(
            ComplexList list,
            Component cell,
            boolean isEditing) {
    }
}
