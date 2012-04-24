package org.ethnochat.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import org.ethnochat.application.EthnoChatApp;
import org.ethnochat.util.ResourceManager;

public class ECTagMgmtPanel extends DockablePanel {

	private static final long serialVersionUID = 100192718606723795L;

	private TagManagerListModelAdapter tagListModel;
    private ComplexList tagList;
    private JButton addTagButton;
    private JButton removeTagButton;

    public ECTagMgmtPanel(EthnoChatApp appInstance) {

        ResourceBundle res = ResourceManager.getControlResources();
        setName(res.getString("TAG_MGMT_WND_TITLE"));

        ECTagMgmtPanelActionListener al = new ECTagMgmtPanelActionListener();

        addTagButton = new JButton("+");
        addTagButton.addActionListener(al);
        removeTagButton = new JButton("-");
        removeTagButton.addActionListener(al);

        tagListModel =
            new TagManagerListModelAdapter(
                appInstance.getCurrentProject().getTagManager());
        tagList = new ComplexList(tagListModel);
        tagList.setListCellRenderer(
            new TagListComplexListCellRenderer());

        layoutComponents();
        setControlState();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tagList.scrollRectToVisible(
                    new java.awt.Rectangle(0, 0, 1, 1));
            }
        });
    }

    private void layoutComponents() {
        ResourceBundle res = ResourceManager.getControlResources();

        GridBagLayout gb = new GridBagLayout();
        setLayout(gb);

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 1.0;
        JScrollPane scrollPane = new JScrollPane(tagList);
        scrollPane.setHorizontalScrollBarPolicy(
            javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(
            javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        gb.setConstraints(scrollPane, c);
        add(scrollPane);

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 0.0;
        javax.swing.JLabel tagButtonsLabel =
            new javax.swing.JLabel(res.getString("TAGS_LBL"));
        gb.setConstraints(tagButtonsLabel, c);
        add(tagButtonsLabel);

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 0.0;

        gb.setConstraints(addTagButton, c);
        add(addTagButton);

        gb.setConstraints(removeTagButton, c);
        add(removeTagButton);
    }

    private void setControlState() {
        if (tagListModel.getSize() == 0) {
            addTagButton.setEnabled(true);
            removeTagButton.setEnabled(false);
        } else {
            addTagButton.setEnabled(true);
            removeTagButton.setEnabled(true);
        }
    }

    private class ECTagMgmtPanelActionListener
            implements java.awt.event.ActionListener {

        public void actionPerformed(java.awt.event.ActionEvent event) {
            if (event.getSource() == addTagButton) {
                tagListModel.addElement(new org.ethnochat.project.Tag());
                setControlState();
            } else if (event.getSource() == removeTagButton) {
                tagListModel.remove(tagList.getSelectedIndex());
                setControlState();
            }
        }
    }
}
