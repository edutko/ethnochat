package org.ethnochat.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;

public class DockingDesktopManager extends DefaultDesktopManager {

	private static final long serialVersionUID = -3357562550671566102L;

	private DockingArea[] dockArea = {new DockingArea(DockingArea.HORIZONTAL),
            new DockingArea(DockingArea.VERTICAL),
            new DockingArea(DockingArea.HORIZONTAL),
            new DockingArea(DockingArea.VERTICAL)};
    private JDesktopPane parentPane;

    public DockingDesktopManager(JDesktopPane parent) {
        parentPane = parent;
        parentPane.addComponentListener(new DockingDesktopListener());
        parentPane.add(dockArea[0], BorderLayout.NORTH);
        parentPane.add(dockArea[1], BorderLayout.WEST);
        parentPane.add(dockArea[2], BorderLayout.SOUTH);
        parentPane.add(dockArea[3], BorderLayout.EAST);
        setDockBounds();
    }

    @Override
	public void beginDraggingFrame(JComponent f) {
        super.beginDraggingFrame(f);
    }

    public void setDockBounds() {

        Dimension d = parentPane.getSize();
        dockArea[0].setLocation(0, 0);
        dockArea[1].setLocation(0, 50);
        dockArea[2].setLocation(0, (int)d.getHeight() - 50);
        dockArea[3].setLocation((int)d.getWidth() - 50, 50);

        dockArea[0].setSize((int)d.getWidth(), 50);
        dockArea[1].setSize(50, (int)d.getHeight() - 100);
        dockArea[2].setSize((int)d.getWidth(), 50);
        dockArea[3].setSize(50, (int)d.getHeight() - 100);
    }

    private class DockingDesktopListener implements ComponentListener {

        public DockingDesktopListener() {}

        public void componentResized(ComponentEvent event) {
            setDockBounds();
        }

        public void componentMoved(ComponentEvent event) {}
        public void componentHidden(ComponentEvent event) {}
        public void componentShown(ComponentEvent event) {}
    }
}
