package org.ethnochat.ui;

public interface ProgressIndicator {

    public void setMinimum(int value);
    public void setMaximum(int value);
    public void setValue(int value);
}
