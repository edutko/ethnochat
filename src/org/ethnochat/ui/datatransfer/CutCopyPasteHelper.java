package  org.ethnochat.ui.datatransfer;

import javax.swing.Action;

public class CutCopyPasteHelper {

    private static CCPHelperAction cutAction = new CutHelperAction(); 
    private static CCPHelperAction copyAction = new CopyHelperAction(); 
    private static CCPHelperAction pasteAction = new PasteHelperAction(); 

    public static Action getCutAction() {
        return cutAction;
    }

    public static Action getCopyAction() {
        return copyAction;
    }

    public static Action getPasteAction() {
        return pasteAction;
    }
}
