package org.ethnochat.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JList;
import javax.swing.JScrollPane;

import org.ethnochat.application.EthnoChatApp;
import org.ethnochat.project.Project;
import org.ethnochat.project.Transcript;
import org.ethnochat.project.event.ConversationManagerEvent;
import org.ethnochat.project.event.ConversationManagerListener;
import org.ethnochat.util.ResourceManager;

public class ECTranscriptListPanel extends DockablePanel
        implements ConversationManagerListener {

	private static final long serialVersionUID = -5050472358571939245L;

	private final EthnoChatApp appInstance;
    private JList transcriptList;

    public ECTranscriptListPanel(EthnoChatApp appInstance) {
        this.appInstance = appInstance;

        ResourceBundle res = ResourceManager.getControlResources();
        setName(res.getString("TRANSCRIPT_LIST_WND_TITLE"));

        transcriptList =
            new JList(appInstance.getCurrentProject().getTranscriptList());
        transcriptList.addMouseListener(new TranscriptListMouseListener());

        layoutComponents();
    }

    public void conversationStarted(ConversationManagerEvent event) {
        transcriptList.setListData(
            appInstance.getCurrentProject().getTranscriptList());
    }

    public void conversationEnded(ConversationManagerEvent event) {}

    private void layoutComponents() {
        GridBagLayout gb = new GridBagLayout();
        setLayout(gb);

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.weightx = 1.0;
        c.weighty = 1.0;

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(transcriptList);
        gb.setConstraints(scrollPane, c);
        add(scrollPane);

        setMinimumSize(getPreferredSize());
    }

    private class TranscriptListMouseListener extends MouseAdapter {

        @Override
		public void mousePressed(MouseEvent event) {
            if (event.getClickCount() == 2) {
                String str = (String)transcriptList.getSelectedValue();
                Project project = appInstance.getCurrentProject();
                File file =
                    new File(
                        project.getConversationTranscriptDirectory(),
                        str);
                Transcript transcript =
                    new Transcript(project, file);
                try {
                    transcript.loadFromFile();
                } catch (java.io.IOException ignored) {
                } catch (org.xml.sax.SAXException e) {
                    // TO DO: handle exception
                }
                appInstance.getMainWindow().createTranscriptWindow(transcript);
            }
        }
     };
}
