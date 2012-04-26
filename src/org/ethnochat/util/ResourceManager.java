package org.ethnochat.util;

import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ResourceManager {

    private static PropertyResourceBundle controlRes;

    public static void initialize() {
        loadResources();
    }

    public static ResourceBundle getControlResources() {

        if (controlRes == null) {
            initialize();
        }

        return controlRes;
    }

    private static void loadResources() {

        try {

            controlRes = (PropertyResourceBundle)ResourceBundle.getBundle("res/controls");

        } catch (MissingResourceException e) {
            throw new RuntimeException(
                "Some of the application resources (text, icons, etc.) are "
                + "missing.\n"
                + "Try reinstalling the application. If this problem "
                + "continues, contact technical\n"
                + "support.");
        }
    }
}
