package org.ethnochat.ui;

import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.MenuComponent;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.ethnochat.application.EthnoChatApp;
import org.ethnochat.util.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
    The code for making the JInternalFrame behave like a modal dialog was
    adapted from the article "Creating Modal Internal Frames -- Approach 1 and
    Approach 2" on the Sun Developer Network:

    http://java.sun.com/developer/JDCTechTips/2001/tt1220.html
*/
public class ECStartupDialog extends javax.swing.JInternalFrame {

	private static final long serialVersionUID = 2995094305082400710L;

	private final EthnoChatApp appInstance;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ECParentFrame owner;
    private JRadioButton newProjectButton;
    private JRadioButton recentProjectButton;
    private JRadioButton openProjectButton;
    private JList recentProjectList;
    private JTextField projectFileNameTF;
    private JButton browseButton;
    private JButton okButton;
    private JButton cancelButton;
    private File selectedFile;

    public ECStartupDialog(EthnoChatApp appInstance, ECParentFrame owner) {
        this.appInstance = appInstance;
        this.owner = owner;
        this.selectedFile = null;

        ResourceBundle res = ResourceManager.getControlResources();
        setName(res.getString("STARUP_DLG_TITLE"));

        layoutComponents();

        newProjectButton.setSelected(true);
        setClosable(true);
        setSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        pack();

        addInternalFrameListener(new ECStartupDialogFrameListener());
        final JPanel glass = new JPanel();
        glass.setOpaque(false);
        javax.swing.event.MouseInputAdapter adapter =
            new javax.swing.event.MouseInputAdapter(){};
        glass.addMouseListener(adapter);
        glass.addMouseMotionListener(adapter);

        putClientProperty("JInternalFrame.frameType", "optionDialog");
        java.awt.Dimension size = getPreferredSize();
        java.awt.Dimension rootSize = owner.getContentPane().getSize();
        setBounds((rootSize.width - size.width) / 2,
            (rootSize.height - size.height) / 2,
            size.width, size.height);
        owner.getContentPane().validate();
        try {
          setSelected(true);
        } catch (java.beans.PropertyVetoException ignored) {}

        glass.add(this);
        owner.getRootPane().setGlassPane(glass);
        glass.setVisible(true);
    }

    @Override public void setVisible(boolean value) {
        super.setVisible(value);
        if (value) {
            startModal();
        } else {
            stopModal();
        }
    }

    public File getSelectedProjectFile() {
        return selectedFile;
    }

    private void layoutComponents() {
        ResourceBundle res = ResourceManager.getControlResources();
        ECStartupDialogActionListener al = new ECStartupDialogActionListener();

        java.awt.GridBagLayout gb = new java.awt.GridBagLayout();
        setLayout(gb);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new java.awt.Insets(10, 10, 10, 10);
        c.weighty = 0.0;
        c.weightx = 1.0;

        javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();

        newProjectButton =
            new JRadioButton(res.getString("NEW_PROJECT_LBL"));
        newProjectButton.addActionListener(
            new ECStartupDialogActionListener());
        group.add(newProjectButton);
        gb.setConstraints(newProjectButton, c);
        add(newProjectButton);

        c.insets = new java.awt.Insets(10, 10, 0, 10);

        recentProjectButton =
            new JRadioButton(res.getString("RECENT_PROJECT_LBL"));
        recentProjectButton.addActionListener(
            new ECStartupDialogActionListener());
        group.add(recentProjectButton);
        gb.setConstraints(recentProjectButton, c);
        add(recentProjectButton);

        c.insets = new java.awt.Insets(0, 20, 10, 10);

        Object[] recentFiles = appInstance.getRecentProjectFiles().toArray();
        recentProjectList = new JList(recentFiles);
        recentProjectList.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        recentProjectList.setPrototypeCellValue("filename.xml");
        recentProjectList.addListSelectionListener(
            new RecentProjectListSelectionListener());
        if (recentFiles.length == 0) {
            // No recently-opened project files
            recentProjectList.setEnabled(false);
            recentProjectButton.setEnabled(false);
        }
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        scrollPane.getViewport().setView(recentProjectList);
        gb.setConstraints(scrollPane, c);
        add(scrollPane);

        c.insets = new java.awt.Insets(10, 10, 0, 10);

        openProjectButton =
            new JRadioButton(res.getString("OPEN_PROJECT_LBL"));
        openProjectButton.addActionListener(
            new ECStartupDialogActionListener());
        group.add(openProjectButton);
        gb.setConstraints(openProjectButton, c);
        add(openProjectButton);

        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new java.awt.Insets(0, 20, 10, 10);

        projectFileNameTF = new JTextField();
        projectFileNameTF.setColumns(30);
        projectFileNameTF.getDocument().addDocumentListener(
            new ECStartupDialogDocumentListener());
        gb.setConstraints(projectFileNameTF, c);
        add(projectFileNameTF);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.NONE;
        c.insets = new java.awt.Insets(10, 10, 10, 10);
        c.weightx = 0.0;

        browseButton = new JButton(res.getString("BROWSE"));
        browseButton.addActionListener(al);
        gb.setConstraints(browseButton, c);
        add(browseButton);

        JPanel buttonPanel = new JPanel();
        gb.setConstraints(buttonPanel, c);

        okButton = new JButton(res.getString("OK"));
        okButton.addActionListener(al);
        buttonPanel.add(okButton);

        cancelButton = new JButton(res.getString("CANCEL"));
        cancelButton.addActionListener(al);
        buttonPanel.add(cancelButton);

        add(buttonPanel);
    }

    private synchronized void startModal() {
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                java.awt.EventQueue theQueue =
                    getToolkit().getSystemEventQueue();
                while (isVisible()) {
                    java.awt.AWTEvent event = theQueue.getNextEvent();
                    Object source = event.getSource();
                    if (event instanceof ActiveEvent) {
                        ((ActiveEvent)event).dispatch();
                    } else if (source instanceof Component) {
                        ((Component)source).dispatchEvent(event);
                    } else if (source instanceof MenuComponent) {
                        ((MenuComponent)source).dispatchEvent(event);
                    } else {
                        log.error("Unable to dispatch: " + event);
                    }
                }
            } else {
                while (isVisible()) {
                    wait();
                }
            }
        } catch (InterruptedException ignored) {}
    }

    private synchronized void stopModal() {
        notifyAll();
    }

    private class ECStartupDialogActionListener
            implements java.awt.event.ActionListener {

        public void actionPerformed(java.awt.event.ActionEvent event) {
            if (event.getSource() == okButton) {
                if (newProjectButton.isSelected()) {
                    selectedFile = null;
                } else if (recentProjectButton.isSelected()) {
                    selectedFile =
                        (File)recentProjectList.getSelectedValues()[0];
                } else if (openProjectButton.isSelected()) {
                    selectedFile = new File(projectFileNameTF.getText());
                }
                try {
                    setClosed(true);
                } catch (java.beans.PropertyVetoException e) {
                    setVisible(false);
                    owner.getGlassPane().setVisible(false);
                }
            } else if (event.getSource() == cancelButton) {
                selectedFile = null;
                try {
                    setClosed(true);
                } catch (java.beans.PropertyVetoException e) {
                    setVisible(false);
                    owner.getGlassPane().setVisible(false);
                }
            } else if (event.getSource() == browseButton) {
                javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
                int returnVal = fc.showOpenDialog(owner);
                if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                    projectFileNameTF.setText(fc.getSelectedFile().getPath());
                }
            } else if (event.getSource() == recentProjectButton
                    && recentProjectList.getSelectedIndex() == -1) {
                recentProjectList.setSelectedIndex(0);
            }
        }
    }

    private class ECStartupDialogDocumentListener
            implements javax.swing.event.DocumentListener {

        public void changedUpdate(javax.swing.event.DocumentEvent event) {}

        public void insertUpdate(javax.swing.event.DocumentEvent event) {
            openProjectButton.setSelected(true);
        }

        public void removeUpdate(javax.swing.event.DocumentEvent event) {}
     }

    private class ECStartupDialogFrameListener
            extends javax.swing.event.InternalFrameAdapter {
        @Override
		public void internalFrameClosed(
                javax.swing.event.InternalFrameEvent event) {
            setVisible(false);
            owner.getGlassPane().setVisible(false);
        }
    }

    private class RecentProjectListSelectionListener
            implements javax.swing.event.ListSelectionListener {
        public void valueChanged(javax.swing.event.ListSelectionEvent event) {
            recentProjectButton.setSelected(true);
        }
    }
}
