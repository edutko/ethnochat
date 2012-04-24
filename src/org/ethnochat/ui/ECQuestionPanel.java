package org.ethnochat.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import org.ethnochat.application.EthnoChatApp;
import org.ethnochat.util.ResourceManager;

public class ECQuestionPanel extends DockablePanel {

	private static final long serialVersionUID = 6475246668093925383L;

	private final EthnoChatApp appInstance;

    private QuestionManagerListModelAdapter questionListModel;
    private ComplexList questionList;
    private JButton addQuestionButton;
    private JButton removeQuestionButton;
    private JButton addGroupButton;
    private JButton removeGroupButton;

    public ECQuestionPanel(EthnoChatApp appInstance) {
        this.appInstance = appInstance;

        ResourceBundle res = ResourceManager.getControlResources();
        setName(res.getString("QUESTION_WND_TITLE"));

        ECQuestionPanelActionListener al = new ECQuestionPanelActionListener();

        addQuestionButton = new JButton("+");
        addQuestionButton.addActionListener(al);
        removeQuestionButton = new JButton("-");
        removeQuestionButton.addActionListener(al);

        addGroupButton = new JButton("+");
        addGroupButton.addActionListener(al);
        removeGroupButton = new JButton("-");
        removeGroupButton.addActionListener(al);

        questionListModel =
            new QuestionManagerListModelAdapter(
                appInstance.getCurrentProject().getQuestionManager());
        javax.swing.ActionMap am = new javax.swing.ActionMap();
        am.put("copyQuestion", new CopyQuestionAction());
        questionList = new ComplexList(questionListModel, am);
        questionList.setListCellRenderer(
            new QuestionListComplexListCellRenderer());

        layoutComponents();
        setControlState();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                questionList.scrollRectToVisible(
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
        JScrollPane scrollPane = new JScrollPane(questionList);
        scrollPane.setHorizontalScrollBarPolicy(
            javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(
            javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        gb.setConstraints(scrollPane, c);
        add(scrollPane);

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.weighty = 0.0;
        javax.swing.JLabel questionButtonsLabel =
            new javax.swing.JLabel(res.getString("QUESTIONS_LBL"));
        gb.setConstraints(questionButtonsLabel, c);
        add(questionButtonsLabel);

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 0.0;
        javax.swing.JLabel groupButtonsLabel =
            new javax.swing.JLabel(res.getString("GROUPS_LBL"));
        gb.setConstraints(groupButtonsLabel, c);
        add(groupButtonsLabel);

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 0.0;

        gb.setConstraints(addQuestionButton, c);
        add(addQuestionButton);

        gb.setConstraints(removeQuestionButton, c);
        add(removeQuestionButton);

        gb.setConstraints(addGroupButton, c);
        add(addGroupButton);

        gb.setConstraints(removeGroupButton, c);
        add(removeGroupButton);
    }

    private void setControlState() {
        if (questionListModel.getSize() == 0) {
            addQuestionButton.setEnabled(false);
            removeQuestionButton.setEnabled(false);
        } else {
            addQuestionButton.setEnabled(true);
            removeQuestionButton.setEnabled(true);
        }
    }

    private class ECQuestionPanelActionListener
            implements java.awt.event.ActionListener {

        public void actionPerformed(java.awt.event.ActionEvent event) {
            if (event.getSource() == addQuestionButton) {
                questionListModel.add(
                    questionList.getSelectedIndex(),
                    new org.ethnochat.project.Question());
                setControlState();
            } else if (event.getSource() == removeQuestionButton) {
                questionListModel.remove(questionList.getSelectedIndex());
                setControlState();
            } else if (event.getSource() == addGroupButton) {
                questionListModel.add(
                    questionList.getSelectedIndex(),
                    new org.ethnochat.project.QuestionGroup());
                setControlState();
            } else if (event.getSource() == removeGroupButton) {
                questionListModel.remove(questionList.getSelectedIndex());
                setControlState();
            }
        }
    }

    private class CopyQuestionAction extends javax.swing.AbstractAction {

		private static final long serialVersionUID = 6596628773182051325L;

		public void actionPerformed(java.awt.event.ActionEvent event) {
            java.awt.Component c = (java.awt.Component)event.getSource();
            java.awt.Container parent = c.getParent();
            org.ethnochat.project.Question question =
                (org.ethnochat.project.Question)questionListModel.get(
                    questionList.indexOf(parent));
            appInstance
                .getMainWindow()
                    .copyQuestionToActiveConversation(question);
        }
    }
}
