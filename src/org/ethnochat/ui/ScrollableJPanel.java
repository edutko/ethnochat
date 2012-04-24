package org.ethnochat.ui;

class ScrollableJPanel extends javax.swing.JPanel
        implements javax.swing.Scrollable {

	private static final long serialVersionUID = 3377279454748946063L;

	private boolean scrollableTracksViewportHeight = false;
    private boolean scrollableTracksViewportWidth = true;

    public ScrollableJPanel() {}

    public java.awt.Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect,
            int orientation,
            int direction) {
        return 10;
    }

    public boolean getScrollableTracksViewportHeight() {
        return scrollableTracksViewportHeight;
    }

    public boolean getScrollableTracksViewportWidth() {
        return scrollableTracksViewportWidth;
    }

    public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect,
            int orientation,
            int direction) {
        return 10;
    }
}
