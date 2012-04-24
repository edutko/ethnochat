package org.ethnochat.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;

import javax.swing.JTextArea;
import javax.swing.text.Highlighter;

import org.ethnochat.project.Question;

class QuestionListComplexListCellRenderer implements ComplexListCellRenderer {

    private static final javax.swing.border.Border QUESTION_BORDER =
        javax.swing.BorderFactory.createMatteBorder(
            1, 0, 0, 0, new java.awt.Color(0xcc, 0xcc, 0xff));

    private static final javax.swing.border.Border GROUP_BORDER =
        javax.swing.BorderFactory.createMatteBorder(
            2, 0, 0, 0, new java.awt.Color(0x00, 0x00, 0x00));


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

		private static final long serialVersionUID = 4326373528305801535L;

		private ComplexList list;
        private Object value;
        private javax.swing.JButton collapseButton;
        private javax.swing.JButton copyButton;
        private JTextArea textArea;
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

            if (value instanceof Question) {
                copyButton = new javax.swing.JButton();
                copyButton.addActionListener(new CellComponentActionListener());
                copyButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
                javax.swing.ActionMap am = list.getListCellActionMap();
                if (am != null) {
                    copyButton.setAction(am.get("copyQuestion"));
                }
                copyButton.setText(">>");
            } else {
                collapseButton = new javax.swing.JButton("-");
                collapseButton.addActionListener(new CellComponentActionListener());
                collapseButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
            }

            textArea = new JTextArea();
            textArea.addMouseListener(new CellComponentMouseListener());
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

            if (value instanceof Question) {
                layoutComponentsForQuestion();
            } else {
                layoutComponentsForGroup();
            }
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
                if (wasEditing) {
                    QuestionManagerListModelAdapter qMgrListModel =
                        (QuestionManagerListModelAdapter)list.getModel();
                    qMgrListModel.changeElementText(
                        value, textArea.getText());
                }
            }
        }

        private void layoutComponentsForQuestion() {
            java.awt.GridBagLayout gb = new java.awt.GridBagLayout();
            setLayout(gb);

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.gridwidth = 1;
            //c.insets = new java.awt.Insets(10, 10, 10, 10);
            c.weighty = 0.0;
            c.weightx = 1.0;

            gb.setConstraints(textArea, c);
            add(textArea);

            c.fill = GridBagConstraints.VERTICAL;
            c.gridwidth = GridBagConstraints.REMAINDER;
            //c.insets = new java.awt.Insets(10, 10, 10, 10);
            c.weighty = 0.0;
            c.weightx = 0.0;

            gb.setConstraints(copyButton, c);
            add(copyButton);

            setBorder(QUESTION_BORDER);
        }

        private void layoutComponentsForGroup() {
            java.awt.GridBagLayout gb = new java.awt.GridBagLayout();
            setLayout(gb);

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.NONE;
            c.gridwidth = 1;
            //c.insets = new java.awt.Insets(10, 10, 10, 10);
            c.weighty = 0.0;
            c.weightx = 0.0;

            gb.setConstraints(collapseButton, c);
            add(collapseButton);

            c.fill = GridBagConstraints.BOTH;
            c.gridwidth = GridBagConstraints.REMAINDER;
            //c.insets = new java.awt.Insets(10, 10, 10, 10);
            c.weighty = 0.0;
            c.weightx = 1.0;

            textArea.setFont(
                textArea.getFont().deriveFont(java.awt.Font.BOLD));
            gb.setConstraints(textArea, c);
            add(textArea);

            setBorder(GROUP_BORDER);
        }

        private class CellComponentActionListener
                implements java.awt.event.ActionListener {

            public void actionPerformed(java.awt.event.ActionEvent event) {
                if (event.getSource().equals(collapseButton)) {

                } else if (event.getSource().equals(copyButton)) {

                }
            }
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
}
