package org.ethnochat.io;

import java.io.File;
import java.io.FileFilter;

public class FileFilterUsingExtension implements FileFilter {

    private String ext;

    public FileFilterUsingExtension(String extension) {
        ext = "." + extension;
    }

    public boolean accept(File pathname) {

        boolean matchesFilter = false;

        if (pathname.isFile()) {
            if (pathname.getName().endsWith(ext)) {
                matchesFilter = true;
            }
        }
        return matchesFilter;
    }
}
