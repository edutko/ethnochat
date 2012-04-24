package org.ethnochat.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class DockedPanel extends JPanel {

	private static final long serialVersionUID = -6467953916234549281L;

	private static ImageIcon pinIcon;
    private static ImageIcon exIcon;

    Container contentPanel;
    JButton closeButton;
    JButton undockButton;

    public DockedPanel(Container panel) {
        contentPanel = panel;
        loadButtonIcons();
        layoutComponents();
    }

    @Override
	public String getName() {
        return contentPanel.getName();
    }

    public Container getContentPanel() {
        return contentPanel;
    }

    private void layoutComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.LINE_AXIS));
        closeButton = new JButton(exIcon);
        closeButton.setMargin(new Insets(0, 0, 0, 0));
        undockButton = new JButton(pinIcon);
        undockButton.setMargin(new Insets(0, 0, 0, 0));
        controlPanel.add(Box.createHorizontalGlue());
        controlPanel.add(undockButton);
        controlPanel.add(closeButton);
        controlPanel.add(Box.createRigidArea(new Dimension(4, 0)));

        add(controlPanel);
        add(contentPanel);
        setSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
    }

    private static synchronized void loadButtonIcons() {
        if (pinIcon == null) {
            java.net.URL url = DockedPanel.class.getResource("/btn_pin.gif");
            if (url == null) {
                throw new RuntimeException("Missing resource: btn_pin.gif");
            }
            pinIcon = new ImageIcon(url);
        }

        if (exIcon == null) {
            java.net.URL url = DockedPanel.class.getResource("/btn_ex.gif");
            if (url == null) {
                throw new RuntimeException("Missing resource: btn_ex.gif");
            }
            exIcon = new ImageIcon(url);
        }
    }
}
