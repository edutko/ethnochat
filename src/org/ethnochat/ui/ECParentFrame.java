package org.ethnochat.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameEvent;

import org.ethnochat.application.EthnoChatApp;
import org.ethnochat.ui.datatransfer.CutCopyPasteHelper;
import org.ethnochat.util.ResourceManager;

public class ECParentFrame extends JFrame
        implements org.ethnochat.project.event.ConversationManagerListener {

	private static final long serialVersionUID = -7225100426204453807L;

	private final EthnoChatApp appInstance;

    private JDesktopPane desktopPane;
    private ECLoginPanel loginPanel;
    private ECContactPanel contactPanel;
    private ECQuestionPanel questionPanel;
    private ECTagMgmtPanel tagMgmtPanel;
    private ECTranscriptListPanel transcriptListPanel;
    private JCheckBoxMenuItem contactPanelMenuItem;
    private JCheckBoxMenuItem loginPanelMenuItem;
    private JCheckBoxMenuItem questionPanelMenuItem;
    private JCheckBoxMenuItem tagMgmtPanelMenuItem;
    private JCheckBoxMenuItem transcriptListPanelMenuItem;

    private ECMessagingPanel lastActiveMessagingPanel;

    public ECParentFrame(EthnoChatApp appInstance) {
        super("EthnoChat");
        this.appInstance = appInstance;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new MainWindowListener());

        layoutComponents();

        Properties prop = appInstance.getProperties();
        String hStr = prop.getProperty("MainWindowHeight");
        String wStr = prop.getProperty("MainWindowWidth");
        if (hStr != null && wStr != null) {
            int h = Integer.parseInt(hStr);
            int w = Integer.parseInt(wStr);
            setSize(new java.awt.Dimension(w, h));
        }

        String xStr = prop.getProperty("MainWindowX");
        String yStr = prop.getProperty("MainWindowY");
        if (xStr != null && yStr != null) {
            int x = Integer.parseInt(xStr);
            int y = Integer.parseInt(yStr);
            setLocation(new java.awt.Point(x, y));
        }
    }

    public void showStartupDialog() {
        ECStartupDialog dlg = new ECStartupDialog(appInstance, this);
        dlg.setVisible(true);
        java.io.File projectFile = dlg.getSelectedProjectFile();
        if (projectFile != null) {
            appInstance.openProject(projectFile);
        }
    }

    public void showLoginPanel(boolean show) {
        if (show) {
            if (loginPanel == null) {
                loginPanel = new ECLoginPanel(appInstance);
            }
            showPanelInWindow(loginPanel, false);
        } else {
            if (loginPanel != null) {
                JInternalFrame f = loginPanel.getParentInternalFrame();
                if (f != null) {
                    f.dispose();
                }
             }
        }
        loginPanelMenuItem.setSelected(show);
    }

    public void showContactPanel(boolean show) {
        if (show) {
            if (contactPanel == null) {
                contactPanel = new ECContactPanel(appInstance);
            }
            showPanelInWindow(contactPanel, true);
        } else {
            if (contactPanel != null) {
                JInternalFrame f = contactPanel.getParentInternalFrame();
                if (f != null) {
                    f.dispose();
                }
             }
        }
        contactPanelMenuItem.setSelected(show);
    }

    public void showQuestionPanel(boolean show) {
        if (show) {
            if (questionPanel == null) {
                questionPanel = new ECQuestionPanel(appInstance);
            }
            showPanelInWindow(questionPanel, true);
        } else {
            if (questionPanel != null) {
                JInternalFrame f = questionPanel.getParentInternalFrame();
                if (f != null) {
                    f.dispose();
                }
             }
        }
        questionPanelMenuItem.setSelected(show);
    }

    public void showTagMgmtPanel(boolean show) {
        if (show) {
            if (tagMgmtPanel == null) {
                tagMgmtPanel = new ECTagMgmtPanel(appInstance);
            }
            showPanelInWindow(tagMgmtPanel, true);
        } else {
            if (tagMgmtPanel != null) {
                JInternalFrame f = tagMgmtPanel.getParentInternalFrame();
                if (f != null) {
                    f.dispose();
                }
             }
        }
        tagMgmtPanelMenuItem.setSelected(show);
    }

    public void showTranscriptPanel(boolean show) {
        if (show) {
            if (transcriptListPanel == null) {
                transcriptListPanel = new ECTranscriptListPanel(appInstance);
            }
            showPanelInWindow(transcriptListPanel, true);
        } else {
            if (transcriptListPanel != null) {
                JInternalFrame f = transcriptListPanel.getParentInternalFrame();
                if (f != null) {
                    f.dispose();
                }
             }
        }
        transcriptListPanelMenuItem.setSelected(show);
    }

    public void createMessagingWindow(
            org.ethnochat.project.Conversation conversation) {
        ECMessagingPanel messagingPanel =
            new ECMessagingPanel(appInstance, conversation);
        showPanelInWindow(messagingPanel, true);
    }

    public void createTranscriptWindow(
            org.ethnochat.project.Transcript transcript) {
        ECTranscriptPanel transcriptPanel =
            new ECTranscriptPanel(appInstance, transcript);
        showPanelInWindow(transcriptPanel, true);
    }

    public int promptToSaveProject() {
        ResourceBundle res = ResourceManager.getControlResources();
        int choice = JOptionPane.showInternalConfirmDialog(
            desktopPane,
            res.getString("DIRTY_PROJECT_MSG"),
            res.getString("DIRTY_PROJECT_DLG_TITLE"),
            JOptionPane.YES_NO_CANCEL_OPTION);
        return choice;
    }

    public int promptToSaveProjectAs() {
        org.ethnochat.project.Project project = appInstance.getCurrentProject();
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        int choice = fc.showSaveDialog(this);
        if (choice == javax.swing.JFileChooser.APPROVE_OPTION) {
            project.setFile(fc.getSelectedFile());
            project.setDirectory(project.getFile().getParentFile());
        }
        return choice;
    }

    public void restoreOpenWindows() {
        Properties prop = appInstance.getProperties();
        ResourceBundle res = ResourceManager.getControlResources();

        if (prop.getProperty(
                res.getString("QUESTION_WND_TITLE") + "Open") != null) {
            showQuestionPanel(true);
        }

        if (prop.getProperty(
                res.getString("CONTACT_WND_TITLE") + "Open") != null) {
            showContactPanel(true);
        }

        if (prop.getProperty(
                res.getString("TAG_MGMT_WND_TITLE") + "Open") != null) {
            showTagMgmtPanel(true);
        }
    }

    public void storeProperties(Properties p) {
        java.awt.Dimension d = getSize();
        p.setProperty("MainWindowHeight", ((Integer)d.height).toString());
        p.setProperty("MainWindowWidth", ((Integer)d.width).toString());

        java.awt.Point pt = getLocation();
        p.setProperty("MainWindowX", ((Integer)pt.x).toString());
        p.setProperty("MainWindowY", ((Integer)pt.y).toString());

        JInternalFrame[] openWindows = desktopPane.getAllFrames();
        for (JInternalFrame frame : openWindows) {
            ECInternalFrame f = (ECInternalFrame)frame;
            storeWindowProperties(f, true);
        }
    }

    public void copyQuestionToActiveConversation(
            org.ethnochat.project.Question question) {
        if (lastActiveMessagingPanel != null) {
            lastActiveMessagingPanel.insertComposeText(question.getText());
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        lastActiveMessagingPanel
                            .getParentInternalFrame()
                                .setSelected(true);
                    } catch (java.beans.PropertyVetoException ignored) {}
                }
            });
        }
    }

    public void conversationStarted(
            org.ethnochat.project.event.ConversationManagerEvent event) {
        createMessagingWindow(event.getConversation());
    }

    public void conversationEnded(
            org.ethnochat.project.event.ConversationManagerEvent event) {}

    private void showPanelInWindow(DockablePanel p,
            boolean resizable) {
        ECInternalFrame f = new ECInternalFrame(p);
        f.addInternalFrameListener(
            new MainWindowInternalFrameListener());
        f.setResizable(resizable);
        f.setClosable(true);

        Properties prop = appInstance.getProperties();
        String hStr = prop.getProperty(f.getTitle() + "Height");
        String wStr = prop.getProperty(f.getTitle() + "Width");
        if (hStr != null && wStr != null) {
            int h = Integer.parseInt(hStr);
            int w = Integer.parseInt(wStr);
            f.setSize(new java.awt.Dimension(w, h));
        }

        String xStr = prop.getProperty(f.getTitle() + "X");
        String yStr = prop.getProperty(f.getTitle() + "Y");
        if (xStr != null && yStr != null) {
            int x = Integer.parseInt(xStr);
            int y = Integer.parseInt(yStr);
            f.setLocation(new java.awt.Point(x, y));
        }

        add(f);
        f.setVisible(true);
    }

    private void storeWindowProperties(ECInternalFrame f, boolean isOpen) {
        Properties prop = appInstance.getProperties();

        java.awt.Dimension dim = f.getSize();
        prop.setProperty(
            f.getTitle() + "Height", ((Integer)dim.height).toString());
        prop.setProperty(
            f.getTitle() + "Width", ((Integer)dim.width).toString());

        java.awt.Point pt = f.getLocation();
        prop.setProperty(
            f.getTitle() + "X", ((Integer)pt.x).toString());
        prop.setProperty(
            f.getTitle() + "Y", ((Integer)pt.y).toString());

        if (isOpen) {
            prop.setProperty(
                f.getTitle() + "Open", "true");
        } else {
            prop.setProperty(
                f.getTitle() + "Open", "false");
        }
    }

    private void layoutComponents() {

        ResourceBundle res = ResourceManager.getControlResources();

        desktopPane = new JDesktopPane();
        desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        desktopPane.setDesktopManager(new DockingDesktopManager(desktopPane));
        desktopPane.setBackground(new java.awt.Color(0xCC, 0xCc, 0xCC));
        setContentPane(desktopPane);

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu(res.getString("FILE_MENU"));
/*        JMenuItem menuItem = new JMenuItem(res.getString("NEW_PROJECT"));
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(res.getString("OPEN_PROJECT"));
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
*/
        JMenuItem menuItem = new JMenuItem(res.getString("EXIT"));
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    appInstance.exit();
                }
            });
        menu.add(menuItem);
        menuBar.add(menu);

        menu = new JMenu(res.getString("EDIT_MENU"));
        menuItem = new JMenuItem(CutCopyPasteHelper.getCutAction());
        menuItem.setMnemonic(KeyEvent.VK_T);
        menuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
//        menuItem.setActionCommand((String)TransferHandler.getCutAction().
//             getValue(Action.NAME));
        menu.add(menuItem);

        menuItem = new JMenuItem(CutCopyPasteHelper.getCopyAction());
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
//        menuItem.setActionCommand((String)TransferHandler.getCopyAction().
//             getValue(Action.NAME));
        menu.add(menuItem);

        menuItem = new JMenuItem(CutCopyPasteHelper.getPasteAction());
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
//        menuItem.setActionCommand((String)TransferHandler.getPasteAction().
//             getValue(Action.NAME));
        menu.add(menuItem);
        menuBar.add(menu);

        menu = new JMenu(res.getString("CHAT_MENU"));
        JMenu loginSubMenu =
            new JMenu(res.getString("LOGIN_MENU"));
        menu.add(loginSubMenu);

        JMenu logoffSubMenu =
            new JMenu(res.getString("LOGOFF_MENU"));
        menu.add(logoffSubMenu);
        menuBar.add(menu);

        menu = new JMenu(res.getString("VIEW_MENU"));
        ActionListener al = new ViewMenuActionListener();

        questionPanelMenuItem =
            new JCheckBoxMenuItem(res.getString("QUESTION_WND_TITLE"));
        questionPanelMenuItem.addActionListener(al);
        questionPanelMenuItem.setMnemonic(KeyEvent.VK_Q);
        menu.add(questionPanelMenuItem);

        tagMgmtPanelMenuItem =
            new JCheckBoxMenuItem(res.getString("TAG_MGMT_WND_TITLE"));
        tagMgmtPanelMenuItem.addActionListener(al);
        tagMgmtPanelMenuItem.setMnemonic(KeyEvent.VK_T);
        menu.add(tagMgmtPanelMenuItem);
        menuBar.add(menu);

        loginPanelMenuItem =
            new JCheckBoxMenuItem(res.getString("LOGIN_WND_TITLE"));
        loginPanelMenuItem.addActionListener(al);
        loginPanelMenuItem.setMnemonic(KeyEvent.VK_S);
        menu.add(loginPanelMenuItem);
        menuBar.add(menu);

        contactPanelMenuItem =
            new JCheckBoxMenuItem(res.getString("CONTACT_WND_TITLE"));
        contactPanelMenuItem.addActionListener(al);
        contactPanelMenuItem.setMnemonic(KeyEvent.VK_C);
        menu.add(contactPanelMenuItem);

        transcriptListPanelMenuItem =
            new JCheckBoxMenuItem(res.getString("TRANSCRIPT_WND_TITLE"));
        transcriptListPanelMenuItem.addActionListener(al);
        transcriptListPanelMenuItem.setMnemonic(KeyEvent.VK_T);
        menu.add(transcriptListPanelMenuItem);

        menu = new JMenu(res.getString("HELP_MENU"));
        menuItem = new JMenuItem(res.getString("ABOUT"));
        menu.add(menuItem);
        menuBar.add(menu);

        setJMenuBar(menuBar);

        setPreferredSize(new Dimension(500, 500));
        pack();
    }

    private class ViewMenuActionListener implements ActionListener {

        public ViewMenuActionListener() {}

        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == contactPanelMenuItem) {
                showContactPanel(contactPanelMenuItem.getState());
            } else if (event.getSource() == loginPanelMenuItem) {
                showLoginPanel(loginPanelMenuItem.getState());
            } else if (event.getSource() == questionPanelMenuItem) {
                showQuestionPanel(questionPanelMenuItem.getState());
            } else if (event.getSource() == tagMgmtPanelMenuItem) {
                showTagMgmtPanel(tagMgmtPanelMenuItem.getState());
            } else if (event.getSource() == transcriptListPanelMenuItem) {
                showTranscriptPanel(transcriptListPanelMenuItem.getState());
            }
        }
    }

    private class MainWindowInternalFrameListener
        extends javax.swing.event.InternalFrameAdapter {

        MainWindowInternalFrameListener() {}

        @Override
		public void internalFrameActivated(InternalFrameEvent event) {
            ECInternalFrame f = (ECInternalFrame)event.getSource();
            java.awt.Container c = f.getContentPane();
            if (c instanceof ECMessagingPanel) {
                lastActiveMessagingPanel = (ECMessagingPanel)c;
            }
        }

        @Override
		public void internalFrameClosed(InternalFrameEvent event) {
            ECInternalFrame f = (ECInternalFrame)event.getSource();
            java.awt.Container c = f.getContentPane();
            if (c == contactPanel) {
                contactPanelMenuItem.setState(false);
                storeWindowProperties(f, false);
            } else if (c == loginPanel) {
                loginPanelMenuItem.setState(false);
                storeWindowProperties(f, false);
            } else if (c == questionPanel) {
                questionPanelMenuItem.setState(false);
                storeWindowProperties(f, false);
            } else if (c == tagMgmtPanel) {
                tagMgmtPanelMenuItem.setState(false);
                storeWindowProperties(f, false);
            } else if (c == transcriptListPanel) {
                transcriptListPanelMenuItem.setState(false);
                storeWindowProperties(f, false);
            } else if (c instanceof ECMessagingPanel) {
                ECMessagingPanel p = (ECMessagingPanel)c;
                appInstance.getConversationManager().endConversation(
                    p.getConversation());
                if (p == lastActiveMessagingPanel) {
                    lastActiveMessagingPanel = null;
                }
            }
        }
    }

    private class MainWindowListener implements WindowListener {

        public MainWindowListener() {}

        public void windowClosing(WindowEvent e) {
            appInstance.exit();
        }

        public void windowActivated(WindowEvent e) {}
        public void windowClosed(WindowEvent e) {}
        public void windowDeactivated(WindowEvent e) {}
        public void windowDeiconified(WindowEvent e) {}
        public void windowIconified(WindowEvent e) {}
        public void windowOpened(WindowEvent e) {}
    }
}
