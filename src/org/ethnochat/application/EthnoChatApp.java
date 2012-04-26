package org.ethnochat.application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.swing.SwingUtilities;

import org.ethnochat.messaging.ContactManager;
import org.ethnochat.messaging.MessagingServiceManager;
import org.ethnochat.plugin.PluginManager;
import org.ethnochat.project.ConversationManager;
import org.ethnochat.project.Project;
import org.ethnochat.ui.ECParentFrame;
import org.ethnochat.util.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class EthnoChatApp {

    private static final File propertyFile =
        new File(ECAppConstants.SETTINGS_FILE);

    private static EthnoChatApp theApp;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private ECParentFrame mainWindow;
    private Properties properties;
    private Project currentProject;
    private MessagingServiceManager serviceManager;
    private ContactManager contactManager;
    private ConversationManager conversationManager;

    public void createMainWindow() {
        mainWindow = new ECParentFrame(this);
        mainWindow.setVisible(true);
        mainWindow.showStartupDialog();
        mainWindow.restoreOpenWindows();
        conversationManager.addConversationManagerListener(mainWindow);
    }

    public void createProject(File projectFile) {
        currentProject = Project.createNewProject();
        conversationManager.setProject(currentProject);
    }

    public void openProject(File projectFile) {
        try {
            currentProject = Project.loadProjectFromFile(projectFile);
            pushRecentProjectFile(projectFile);
            conversationManager.setProject(currentProject);
        } catch (IOException e) {
            // TODO: handle exception
            System.err.println("Error while opening project.");
            System.err.println(e.toString());
        } catch (SAXException e) {
            // TODO: handle exception
            System.err.println("Error while opening project.");
            System.err.println(e.toString());
        }
    }

    public void saveCurrentProject() {
        try {
            currentProject.storeToFile();
            pushRecentProjectFile(currentProject.getFile());
        } catch (IOException e) {
            // TODO: handle exception
            System.err.println("Error while saving project.");
            System.err.println(e.toString());
        }
    }

    public ContactManager getContactManager() {
        return contactManager;
    }

    public ConversationManager getConversationManager() {
        return conversationManager;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public ECParentFrame getMainWindow() {
        return mainWindow;
    }

    public Properties getProperties() {
        return properties;
    }

    public Collection<File> getRecentProjectFiles() {
        ArrayList<File> recentProjects = new ArrayList<File>();
        for (int i = 0; i < ECAppConstants.NUM_RECENT_PROJECTS; i++) {
            String path =
                properties.getProperty(ECAppConstants.RECENT_PROJECT_TAG + i);
            if (path != null && !path.equals("")) {
                recentProjects.add(new File(path));
            }
        }
        return recentProjects;
    }

    public MessagingServiceManager getMessagingServiceManager() {
        return serviceManager;
    }

    public void exit() {
        conversationManager.endAllConversations();
        boolean exitWhenDone = true;
        boolean saveProject = false;
        if (currentProject.isDirty()) {
            int choice = mainWindow.promptToSaveProject();
            if (choice == javax.swing.JOptionPane.YES_OPTION) {
                saveProject = true;
                exitWhenDone = true;
                if (currentProject.getFile() == null
                        || !currentProject.getFile().exists()) {
                    choice = mainWindow.promptToSaveProjectAs();
                    if (choice == javax.swing.JFileChooser.APPROVE_OPTION) {
                        pushRecentProjectFile(currentProject.getFile());
                    } else {
                        saveProject = false;
                        exitWhenDone = false;
                    }
                }
            } else if (choice == javax.swing.JOptionPane.NO_OPTION) {
                saveProject = false;
                exitWhenDone = true;
            } else if (choice == javax.swing.JOptionPane.CANCEL_OPTION) {
                saveProject = false;
                exitWhenDone = false;
            }
        }

        if (saveProject) {
            try {
                currentProject.storeToFile();
            } catch (IOException e) {
                // TO DO: handle exception
                System.err.println("Error while saving settings.");
                System.err.println(e.toString());
            }
        }

        if (exitWhenDone) {
            try {
                mainWindow.storeProperties(properties);
                saveProperties();
            } catch (IOException e) {
                // TO DO: handle exception
                System.err.println("Error while saving settings.");
                System.err.println(e.toString());
            }

            System.exit(0);
        }
    }

    public void loadProperties() throws IOException {
        if (!propertyFile.exists()) {
            createDefaultPropertyFile();
        }
        java.io.InputStream is = new java.io.FileInputStream(propertyFile);
        properties.loadFromXML(is);
        is.close();
    }

    public void saveProperties() throws IOException {
        java.io.OutputStream os = new java.io.FileOutputStream(propertyFile);
        properties.storeToXML(
            os,
            "EthnoChat settings, updated "
                + java.util.Calendar.getInstance().getTime());
        os.close();
    }

    private EthnoChatApp() {
        currentProject = Project.createNewProject();
        PluginManager pluginManager = PluginManager.getInstance();
        serviceManager = new MessagingServiceManager(pluginManager);
        contactManager = new ContactManager(serviceManager);
        conversationManager =
            new ConversationManager(serviceManager, currentProject);
        properties = new Properties();
    }

    private void pushRecentProjectFile(File projectFile) {
        ArrayList<File> currentFileList =
            new ArrayList<File>(getRecentProjectFiles());
        if (currentFileList.contains(projectFile)) {
            currentFileList.remove(projectFile);
        }
        currentFileList.add(0, projectFile);
        for (int i = 0; i < currentFileList.size(); i++) {
            properties.setProperty(
                ECAppConstants.RECENT_PROJECT_TAG + i,
                currentFileList.get(i).getPath());
        }
    }

    private static void createDefaultPropertyFile() throws IOException {
        propertyFile.createNewFile();
        Properties defaultProperties = new Properties();
        defaultProperties.setProperty("MainWindowHeight", "500");
        defaultProperties.setProperty("MainWindowWidth", "500");
        defaultProperties.setProperty("Question ManagementHeight", "300");
        defaultProperties.setProperty("Question ManagementWidth", "300");
        java.io.OutputStream os = new java.io.FileOutputStream(propertyFile);
        defaultProperties.storeToXML(
            os,
            "Default EthnoChat settings, created "
                + java.util.Calendar.getInstance().getTime());
        os.close();
    }

    private static void loadPlugins(File[] directories) {
        PluginManager pluginManager = PluginManager.getInstance();
        for (int i = 0; i < directories.length; i++) {
            try {
                pluginManager.addPluginDirectory(directories[i]);
            } catch (org.ethnochat.io.NotADirectoryException e) {}
        }
        pluginManager.loadAllPlugins();
    }

    private static void createAndShowGUI() {
        theApp.createMainWindow();
    }

    public static void main(String[] args) {

        try {
            ResourceManager.initialize();
            File[] defaultPluginDirectories =
                { new File("plugins/messaging") };
            loadPlugins(defaultPluginDirectories);
            theApp = new EthnoChatApp();
            theApp.loadProperties();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();
                }
            });
        } catch (Exception e) {
            System.err.println("An unexpected error occurred.");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
