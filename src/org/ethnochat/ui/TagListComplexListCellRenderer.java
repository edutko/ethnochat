package org.ethnochat.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.Highlighter;

class TagListComplexListCellRenderer implements ComplexListCellRenderer {

    private static final javax.swing.border.Border TAG_BORDER =
        javax.swing.BorderFactory.createMatteBorder(
            1, 0, 0, 0, new java.awt.Color(0xcc, 0xcc, 0xff));

    public Component getCellRendererComponent(
            ComplexList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        return new CellComponent(list, value, isSelected);
    }

    public void setSelected(ComplexList list, Component cell, boolean isSelected) {
        ((CellComponent)cell).setSelected(isSelected);
    }

    public void setEditing(
            ComplexList list,
            Component cell,
            boolean isEditing) {
        ((CellComponent)cell).setEditing(isEditing);
    }

    private class CellComponent extends javax.swing.JPanel {

		private static final long serialVersionUID = -2772348652409508652L;

		private ComplexList list;
        private Object value;
        private JTextArea textArea;
        private boolean isSelected;
        private boolean isEditing;
        private Color defaultTextAreaBackground;
        private Color defaultTextAreaForeground;
        private Highlighter defaultHighlighter;

        public CellComponent(
                ComplexList list,
                Object value,
                boolean isSelected) {
            this.list = list;
            this.value = value;
            this.isSelected = isSelected;

            textArea = new JTextArea();
            textArea.addMouseListener(new CellComponentMouseListener());
            textArea.getInputMap().put(
                KeyStroke.getKeyStroke("ENTER"), "endEditing");
            textArea.getActionMap().put(
                "endEditing", new EndEditingAction(this));
            textArea.getInputMap().put(
                KeyStroke.getKeyStroke("F2"), "startEditing");
            textArea.getActionMap().put(
                "startEditing", new StartEditingAction(this));
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setMargin(new java.awt.Insets(2, 2, 2, 2));
            textArea.append(value.toString());

            defaultHighlighter = textArea.getHighlighter();
            textArea.setHighlighter(null);

            defaultTextAreaForeground = textArea.getForeground();
            defaultTextAreaBackground = textArea.getBackground();

            setSelected(isSelected);

            layoutComponents();
        }

        public void setSelected(boolean isSelected) {
            if (isSelected && !isEditing) {
                textArea.setBackground(list.getSelectionBackground());
                textArea.setForeground(list.getSelectionForeground());
            } else {
                setEditing(false);
                textArea.setBackground(defaultTextAreaBackground);
                textArea.setForeground(defaultTextAreaForeground);
            }
            this.isSelected = isSelected;
        }

        public boolean isSelected() {
            return isSelected;
        }

        private void setEditing(boolean isEditing) {
            boolean wasEditing = this.isEditing;
            this.isEditing = isEditing;
            textArea.setEditable(isEditing);
            textArea.getCaret().setVisible(isEditing);
            if (isEditing) {
                textArea.setHighlighter(defaultHighlighter);
                textArea.setBackground(defaultTextAreaBackground);
                textArea.setForeground(defaultTextAreaForeground);
            } else {
                textArea.setHighlighter(null);
                if (isSelected()) {
                    textArea.setBackground(list.getSelectionBackground());
                    textArea.setForeground(list.getSelectionForeground());
                }
                if (wasEditing) {
                    TagManagerListModelAdapter tagMgrListModel =
                        (TagManagerListModelAdapter)list.getModel();
                    tagMgrListModel.changeElementText(
                        value, textArea.getText());
                }
            }
        }

        private void layoutComponents() {
            java.awt.GridBagLayout gb = new java.awt.GridBagLayout();
            setLayout(gb);

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.gridwidth = GridBagConstraints.REMAINDER;
            //c.insets = new java.awt.Insets(10, 10, 10, 10);
            c.weighty = 0.0;
            c.weightx = 1.0;

            gb.setConstraints(textArea, c);
            add(textArea);

            setBorder(TAG_BORDER);
        }
    }

    private class CellComponentMouseListener
            extends java.awt.event.MouseAdapter {

        @Override
		public void mouseClicked(java.awt.event.MouseEvent event) {
            Component c = event.getComponent();
            Component parent = c.getParent();
            java.awt.event.MouseEvent newEvent =
                javax.swing.SwingUtilities.convertMouseEvent(c, event, parent);
            parent.dispatchEvent(newEvent);
        }
    }

    private class StartEditingAction extends javax.swing.AbstractAction {

		private static final long serialVersionUID = 7677439241183615203L;

		private CellComponent cell;

        public StartEditingAction(CellComponent cell) {
            this.cell = cell;
        }

        public void actionPerformed(ActionEvent event) {
            cell.setEditing(true);
        }
    }

    private class EndEditingAction extends javax.swing.AbstractAction {

		private static final long serialVersionUID = -2207311076740858741L;

		private CellComponent cell;

        public EndEditingAction(CellComponent cell) {
            this.cell = cell;
        }

        public void actionPerformed(ActionEvent event) {
            cell.setEditing(false);
        }
    }
}
