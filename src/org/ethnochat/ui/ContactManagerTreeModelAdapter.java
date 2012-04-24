package org.ethnochat.ui;

import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.ethnochat.messaging.Contact;
import org.ethnochat.messaging.ContactGroup;
import org.ethnochat.messaging.ContactManager;
import org.ethnochat.messaging.MessagingService;
import org.ethnochat.messaging.event.ContactManagerEvent;
import org.ethnochat.messaging.event.ContactManagerListener;

class ContactManagerTreeModelAdapter implements TreeModel,
        ContactManagerListener {

    private ContactManager contactMgr;
    private java.util.Vector<TreeModelListener> treeModelListeners;

    public ContactManagerTreeModelAdapter(ContactManager contactMgr) {
        this.contactMgr = contactMgr;
        contactMgr.addContactManagerListener(this);
        treeModelListeners = new java.util.Vector<TreeModelListener>();
    }

    public MessagingService getMessagingServiceForNode(Object node) {
        if (node instanceof MessagingService) {
            return (MessagingService)node;
        } else if (node instanceof ContactGroup) {
            ContactGroup group = (ContactGroup)node;
            return group.getMessagingService();
        } else if (node instanceof Contact) {
            Contact contact = (Contact)node;
            return contact.getMessagingService();
        } else {
            throw new RuntimeException(
                "Invalid node passed to getMessagingServiceForNode().");
        }
    }

    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(l);
    }

    public Object getChild(Object parent, int index) {
        // The API requires that parent is a node from this model. If this
        // assumption is violated, the results are undefined.
        if (parent instanceof ContactManager) {
            return contactMgr.getServices().get(index);
        } else if (parent instanceof MessagingService) {
            MessagingService service = (MessagingService)parent;
            return contactMgr.getGroups(service).get(index);
        } else if (parent instanceof ContactGroup) {
            ContactGroup group = (ContactGroup)parent;
            return contactMgr.getOnlineContacts(group).get(index);
        } else if (parent instanceof Contact) {
            return null;
        } else {
            throw new RuntimeException(
                "Invalid parent node passed to getChild().");
        }
    }

    public int getChildCount(Object parent) {
        // The API requires that parent is a node from this model. If this
        // assumption is violated, the results are undefined.
        if (parent instanceof ContactManager) {
            return contactMgr.getServiceCount();
        } else if (parent instanceof MessagingService) {
            MessagingService service = (MessagingService)parent;
            return contactMgr.getGroupCount(service);
        } else if (parent instanceof ContactGroup) {
            ContactGroup group = (ContactGroup)parent;
            return contactMgr.getOnlineContactCount(group);
        } else if (parent instanceof Contact) {
            return 0;
        } else {
            throw new RuntimeException(
                "Invalid parent node passed to getChildCount().");
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof ContactManager) {
            List<MessagingService> haystack = contactMgr.getServices();
            MessagingService needle = (MessagingService)child;
            return haystack.indexOf(needle);
        } else if (parent instanceof MessagingService) {
            MessagingService service = (MessagingService)parent;
            List<ContactGroup> haystack = contactMgr.getGroups(service);
            ContactGroup needle = (ContactGroup)child;
            return haystack.indexOf(needle);
        } else if (parent instanceof ContactGroup) {
            ContactGroup group = (ContactGroup)parent;
            List<Contact> haystack = contactMgr.getOnlineContacts(group);
            Contact needle = (Contact)child;
            return haystack.indexOf(needle);
        } else {
            return -1;
        }
    }

    public Object getRoot() {
        return contactMgr;
    }

    public boolean isLeaf(Object node) {
        // The API requires that parent is a node from this model. If this
        // assumption is violated, the results are undefined.
        if (node instanceof Contact) {
            return true;
        } else {
            return false;
        }
    }

    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(l);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public void contactAdded(ContactManagerEvent event) {
        TreeModelEvent tme = convertToTreeModelEvent(event);
        fireTreeNodesInserted(tme);
    }

    public void contactChanged(ContactManagerEvent event) {
        TreeModelEvent tme = convertToTreeModelEvent(event);
        switch (event.getType()) {
            case CONTACT_SIGNED_ON:
                fireTreeNodesInserted(tme);
                break;

            case CONTACT_SIGNED_OFF:
                fireTreeNodesRemoved(tme);
                break;

            default:
                fireTreeNodesChanged(tme);
                break;
        }
    }

    public void contactRemoved(ContactManagerEvent event) {
        TreeModelEvent tme = convertToTreeModelEvent(event);
        fireTreeNodesRemoved(tme);
    }

    public void contactGroupAdded(ContactManagerEvent event) {
        TreeModelEvent tme = convertToTreeModelEvent(event);
        fireTreeNodesInserted(tme);
    }

    public void contactGroupChanged(ContactManagerEvent event) {
        TreeModelEvent tme = convertToTreeModelEvent(event);
        fireTreeNodesChanged(tme);
    }

    public void contactGroupRemoved(ContactManagerEvent event) {
        TreeModelEvent tme = convertToTreeModelEvent(event);
        fireTreeNodesRemoved(tme);
    }

    public void messagingServiceChanged(ContactManagerEvent event) {
        TreeModelEvent tme = convertToTreeModelEvent(event);
        fireTreeStructureChanged(tme);
    }

    private TreeModelEvent convertToTreeModelEvent(ContactManagerEvent event) {
        TreeModelEvent tme;
        Contact contact = event.getContact();
        ContactGroup group = event.getContactGroup();
        MessagingService service = event.getMessagingService();
        if (contact == null && group == null) {
            Object parent = getRoot();
            tme = new TreeModelEvent(this,
                new Object[] {parent},
                new int[] {getIndexOfChild(parent, service)},
                new Object[] {service});
        } else if (contact == null) {
            tme = new TreeModelEvent(this,
                new Object[] {getRoot(), service},
                new int[] {getIndexOfChild(service, group)},
                new Object[] {group});
        } else if (group == null) {
            tme = new TreeModelEvent(this,
                new Object[] {getRoot(), service},
                new int[] {getIndexOfChild(service, contact)},
                new Object[] {contact});
        } else {
            tme = new TreeModelEvent(this,
                new Object[] {getRoot(), service, group},
                new int[] {getIndexOfChild(group, contact)},
                new Object[] {contact});
        }
        return tme;
    }

    private void fireTreeNodesChanged(TreeModelEvent event) {
        for (TreeModelListener l : treeModelListeners) {
            l.treeNodesChanged(event);
        }
    }

    private void fireTreeNodesInserted(TreeModelEvent event) {
        for (TreeModelListener l : treeModelListeners) {
            l.treeNodesInserted(event);
        }
    }

    private void fireTreeNodesRemoved(TreeModelEvent event) {
        for (TreeModelListener l : treeModelListeners) {
            l.treeNodesRemoved(event);
        }
    }

    private void fireTreeStructureChanged(TreeModelEvent event) {
        for (TreeModelListener l : treeModelListeners) {
            l.treeStructureChanged(event);
        }
    }
}
