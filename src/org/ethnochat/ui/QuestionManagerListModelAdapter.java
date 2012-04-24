package org.ethnochat.ui;

import java.util.List;

import org.ethnochat.project.Question;
import org.ethnochat.project.QuestionGroup;
import org.ethnochat.project.QuestionManager;
import org.ethnochat.project.event.QuestionManagerEvent;
import org.ethnochat.project.event.QuestionManagerListener;

class QuestionManagerListModelAdapter extends javax.swing.AbstractListModel
        implements QuestionManagerListener {

	private static final long serialVersionUID = 1902207884332818707L;

	private QuestionManager questionMgr;

    public QuestionManagerListModelAdapter(QuestionManager questionMgr) {
        this.questionMgr = questionMgr;
        questionMgr.addQuestionManagerListener(this);
    }

    public void changeElementText(Object o, String text) {
        if (o instanceof Question) {
            Question oldQuestion = (Question)o;
            QuestionGroup group = questionMgr.getGroupForQuestion(oldQuestion);
            questionMgr.changeQuestion(group, oldQuestion, new Question(text));
        } else if (o instanceof QuestionGroup) {
            QuestionGroup group = (QuestionGroup)o;
            questionMgr.changeQuestionGroupName(group, text);
        }
    }

    public Object get(int index) {
        return getElementAt(index);
    }

    public Object getElementAt(int index) {
        if (index < 0 || index >= getSize()) {
            return null;
        }
        int itemCount = 0;
        List<QuestionGroup> groups = questionMgr.getQuestionGroups();
        for (QuestionGroup group : groups) {
            if (itemCount == index) {
                return group;
            } else if (itemCount + group.size() + 1 > index) {
                return group.get(index - itemCount - 1);
            }
            itemCount += group.size() + 1;
        }

        return null;
    }

    public int getSize() {
        int size = questionMgr.getQuestionGroupCount()
            + questionMgr.getQuestionCount();
        return size;
    }

    public void add(int index, Object o) {
        Object previous = getElementAt(index);
        if (o instanceof Question) {
            QuestionGroup group = questionMgr.getQuestionGroup(0);
            int questionIndex = -1;
            if (group == null) {
                throw new IndexOutOfBoundsException();
            } else if (previous instanceof QuestionGroup) {
                group = (QuestionGroup)previous;
                questionIndex = group.size();
            } else if (previous instanceof Question) {
                Question question = (Question)previous;
                group = questionMgr.getGroupForQuestion(question);
                questionIndex = index - indexOf(group) - 1;
            }
            questionMgr.addQuestion(group, (Question)o, questionIndex);
        } else if (o instanceof QuestionGroup) {
            int groupIndex = questionMgr.getQuestionGroupCount();
            if (previous instanceof QuestionGroup) {
                QuestionGroup group = (QuestionGroup)previous;
                groupIndex = questionMgr.indexOfQuestionGroup(group);
            } else if (previous instanceof Question) {
                Question question = (Question)previous;
                QuestionGroup group =
                    questionMgr.getGroupForQuestion(question);
                groupIndex = questionMgr.indexOfQuestionGroup(group);
            }
            questionMgr.addQuestionGroup((QuestionGroup)o, groupIndex);
        }
    }

    public void remove(int index) {
        Object o = getElementAt(index);
        if (o instanceof Question) {
            Question question = (Question)o;
            QuestionGroup group = questionMgr.getGroupForQuestion(question);
            questionMgr.removeQuestion(group, question);
        } else if (o instanceof QuestionGroup) {
            QuestionGroup group = (QuestionGroup)o;
            questionMgr.removeQuestionGroup(group);
        }
    }

    public void questionAdded(QuestionManagerEvent event) {
        Question question = event.getQuestion();
        fireIntervalAdded(this, indexOf(question), indexOf(question));
    }

    public void questionChanged(QuestionManagerEvent event) {
        Question question = event.getQuestion();
        fireContentsChanged(this, indexOf(question), indexOf(question));
    }

    public void questionRemoved(QuestionManagerEvent event) {
        Question question = event.getQuestion();
        fireIntervalRemoved(this, indexOf(question), indexOf(question));
    }

    public void questionGroupAdded(QuestionManagerEvent event) {
        QuestionGroup group = event.getQuestionGroup();
        fireIntervalAdded(this, indexOf(group), indexOf(group));
    }

    public void questionGroupChanged(QuestionManagerEvent event) {
        QuestionGroup group = event.getQuestionGroup();
        fireContentsChanged(this, indexOf(group), indexOf(group));
    }

    public void questionGroupRemoved(QuestionManagerEvent event) {
        QuestionGroup group = event.getQuestionGroup();
        fireIntervalRemoved(
            this,
            indexOf(group),
            indexOf(group) + group.size());
    }

    private int indexOf(Object o) {
        int index = -1;
        if (o instanceof Question) {
            Question question = (Question)o;
            QuestionGroup group = questionMgr.getGroupForQuestion(question);
            index = 0;
            int groupIndex = questionMgr.indexOfQuestionGroup(group);
            for (int i = 0; i < groupIndex; i++) {
                index += questionMgr.getQuestionGroup(i).size() + 1;
            }
            index += questionMgr.indexOfQuestion(group, question) + 1;
        } else if (o instanceof QuestionGroup) {
            QuestionGroup group = (QuestionGroup)o;
            index = 0;
            int groupIndex = questionMgr.indexOfQuestionGroup(group);
            for (int i = 0; i < groupIndex; i++) {
                index += questionMgr.getQuestionGroup(i).size() + 1;
            }
        } else {
            throw
                new RuntimeException(
                    "Invalid object type passed to "
                    + "QuestionManagerListModelAdapter.indexOf()");
        }
        return index;
    }
}
