package org.ethnochat.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ethnochat.project.event.QuestionManagerEvent;
import org.ethnochat.project.event.QuestionManagerListener;
import org.ethnochat.project.event.TagManagerEvent;
import org.ethnochat.project.event.TagManagerListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Project
        implements QuestionManagerListener,
        TagManagerListener {

    private static final String ROOT_TAG = "EthnoChatProject";
    private static final String PROJECT_DIR_TAG = "ProjectDirectory";
    private static final String QUESTION_GROUP_TAG = "QuestionGroup";
    private static final String QUESTION_GROUP_NAME_ATTR = "name";
    private static final String QUESTION_TAG = "Question";
    private static final String TAG_TAG = "Tag";
    private static final String TAG_ID_ATTR = "id";

    private File projectFile;
    private File projectDirectory;
    private transient TagManager tagMgr;
    private transient QuestionManager questionMgr;
    private transient boolean isDirty;

    public static Project loadProjectFromFile(File file)
            throws IOException, SAXException {
        Project proj = new Project(file);
        proj.loadFromFile();
        return proj;
    }

    public static Project createNewProject() {
    	File projDir = new File("Untitled");
    	File projFile = new File(projDir, "Untitled.ecp");
        return createNewProject(projFile, projDir);
    }

    public static Project createNewProject(File file, File directory) {
        Project proj = new Project(file);
        proj.setDirectory(directory);
        proj.isDirty = true;
        return proj;
    }

    public File getFile() {
        return projectFile;
    }

    public File getConversationTranscriptDirectory() {
        return new File(projectDirectory, "transcripts");
    }

    public Vector<String> getTranscriptList() {
        Vector<String> list = new Vector<String>();
        String[] files = getConversationTranscriptDirectory().list();
        if (files != null) {
	        for (int i = 0; i < files.length; i++) {
	            list.add(files[i]);
	        }
	        Collections.sort(list);
        }
        return list;
    }

    public TagManager getTagManager() {
        return tagMgr;
    }

    public QuestionManager getQuestionManager() {
        return questionMgr;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void loadFromFile() throws IOException, SAXException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //dbf.setValidating(true);
            DocumentBuilder parser = dbf.newDocumentBuilder();
            Document doc = parser.parse(projectFile);

            projectDirectory =
                new File(
                    doc.getElementsByTagName(PROJECT_DIR_TAG)
                        .item(0).getTextContent());

            NodeList tagNodes = doc.getElementsByTagName(TAG_TAG);
            for (int i = 0; i < tagNodes.getLength(); i++) {
                Node node = tagNodes.item(i);
                Integer tagID =
                    Integer.parseInt(
                        node.getAttributes()
                            .getNamedItem(TAG_ID_ATTR)
                                .getTextContent());
                String tagText = node.getTextContent();
                Tag tag = new Tag(tagID, tagText);
                tagMgr.addTag(tag);
            }

            NodeList questionGroupNodes =
                doc.getElementsByTagName(QUESTION_GROUP_TAG);
            for (int i = 0; i < questionGroupNodes.getLength(); i++) {
                Node node = questionGroupNodes.item(i);
                QuestionGroup group =
                    new QuestionGroup(
                        node.getAttributes()
                            .getNamedItem(QUESTION_GROUP_NAME_ATTR)
                                .getTextContent());
                NodeList questionNodes =
                    ((Element)node).getElementsByTagName(QUESTION_TAG);
                for (int j = 0; j < questionNodes.getLength(); j++) {
                    group.add(
                        new Question(questionNodes.item(j).getTextContent()));
                }
                questionMgr.addQuestionGroup(group);
            }

            isDirty = false;
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            // TO DO: handle exception
        }
    }

    public void setDirectory(File directory) {
        projectDirectory = directory;
        isDirty = true;
    }

    public void setFile(File file) {
        projectFile = file;
        isDirty = true;
    }

    public void storeToFile() throws IOException {

    	if (!projectDirectory.exists()) {
    		projectDirectory.mkdir();
    	}

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);
        try {
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element root = doc.createElement(ROOT_TAG);
            doc.appendChild(root);

            Element element = doc.createElement(PROJECT_DIR_TAG);
            element.appendChild(doc.createTextNode(projectDirectory.getPath()));
            root.appendChild(element);

            for (Tag tag : tagMgr.getTags()) {
                Element tagElement = doc.createElement(TAG_TAG);
                tagElement.setAttribute(TAG_ID_ATTR, tag.getID().toString());
                tagElement.appendChild(doc.createTextNode(tag.getText()));
                root.appendChild(tagElement);
            }

            for (QuestionGroup group : questionMgr.getQuestionGroups()) {
                Element groupElement = doc.createElement(QUESTION_GROUP_TAG);
                groupElement.setAttribute(
                    QUESTION_GROUP_NAME_ATTR,
                    group.getName());
                root.appendChild(groupElement);
                for (Question question : group) {
                    element = doc.createElement(QUESTION_TAG);
                    element.appendChild(doc.createTextNode(
                        question.toString()));
                    groupElement.appendChild(element);
                }
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            Source src = new DOMSource(doc);
            Result dest = new StreamResult(new FileOutputStream(projectFile));
            serializer.transform(src, dest);

            isDirty = false;
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            // TODO: handle exception
        } catch (javax.xml.transform.TransformerConfigurationException e) {
            // TODO: handle exception
        } catch (javax.xml.transform.TransformerException e) {
            // TODO: handle exception
        }
    }

    public void tagAdded(TagManagerEvent event) {
        isDirty = true;
    }

    public void tagChanged(TagManagerEvent event) {
        isDirty = true;
    }

    public void tagRemoved(TagManagerEvent event) {
        isDirty = true;
    }

    public void questionAdded(QuestionManagerEvent event) {
        isDirty = true;
    }

    public void questionChanged(QuestionManagerEvent event) {
        isDirty = true;
    }

    public void questionRemoved(QuestionManagerEvent event) {
        isDirty = true;
    }

    public void questionGroupAdded(QuestionManagerEvent event) {
        isDirty = true;
    }

    public void questionGroupChanged(QuestionManagerEvent event) {
        isDirty = true;
    }

    public void questionGroupRemoved(QuestionManagerEvent event) {
        isDirty = true;
    }

    private Project(File file) {
        projectFile = file;
        projectDirectory = file.getParentFile();
        tagMgr = new TagManager();
        tagMgr.addTagManagerListener(this);
        questionMgr = new QuestionManager();
        questionMgr.addQuestionManagerListener(this);
        isDirty = false;
    }
}
