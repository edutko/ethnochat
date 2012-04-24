package org.ethnochat.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

public class DockingArea extends Container {

	private static final long serialVersionUID = -6380973455351751594L;

	public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private Container actualContainer;
    private ShadedArea highlight;
    private boolean isHorizontal;

    public DockingArea(int orientation) {
        isHorizontal = (orientation == HORIZONTAL);
        actualContainer = new Container();
        highlight = new ShadedArea();
        add(highlight);
        actualContainer.setSize(getSize());
        add(actualContainer);
    }

    public void dock(Container c) {

        int requiredWidth = c.getMinimumSize().width;
        int requiredHeight = c.getMinimumSize().height;
        if (isHorizontal
                && requiredHeight > getHeight()) {
            setSize(getWidth(), requiredHeight);
        } else if (requiredWidth > getWidth()) {
            setSize(requiredWidth, getHeight());
        }

        if (actualContainer.getComponentCount() == 1) {
            convertToTabbedPane();
        }
        actualContainer.add(new DockedPanel(c));
    }

    public Container undock(Container c) {

        DockedPanel p = (DockedPanel)c;
        Container panel = p.getContentPanel();
        actualContainer.remove(c);
        if (actualContainer.getComponentCount() == 1) {
            convertFromTabbedPane();
        }
        return panel;
    }

    public void setHighlightVisible(boolean visible) {
        if (visible
                && !highlight.isVisible()) {
            highlight.setLocation(0, 0);
            highlight.setSize(getWidth(), getHeight());
        }
        highlight.setVisible(visible);
    }

    public boolean isHighlightVisible() {
        return highlight.isVisible();
    }

    @Override
	public void setSize(int width, int height) {
        super.setSize(width, height);
        actualContainer.setSize(width, height);
    }

    @Override
	public void setSize(Dimension d) {
        super.setSize(d);
        actualContainer.setSize(d);
    }

    private void convertToTabbedPane() {
        Component existingComponent = actualContainer.getComponent(0);
        remove(actualContainer);
        actualContainer = new JTabbedPane();
        actualContainer.add(existingComponent);
        actualContainer.setSize(getSize());
        add(actualContainer);
    }

    private void convertFromTabbedPane() {
        Component existingComponent = actualContainer.getComponent(0);
        remove(actualContainer);
        actualContainer = new Container();
        actualContainer.add(existingComponent);
        actualContainer.setSize(getSize());
        add(actualContainer);
    }

    private class ShadedArea extends JComponent {

		private static final long serialVersionUID = 8941179248891017577L;

		ShadedArea() {
            setVisible(false);
        }

        @Override
		protected void paintComponent(Graphics g) {
            g.setColor(new Color(0, 0, 153, 127));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
