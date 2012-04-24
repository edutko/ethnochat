package org.ethnochat.project.event;

public interface TranscriptListener {

    public void messageAdded(TranscriptEvent event);
    public void tagAdded(TranscriptEvent event);
    public void tagChanged(TranscriptEvent event);
    public void tagRemoved(TranscriptEvent event);
    public void noteAdded(TranscriptEvent event);
    public void noteChanged(TranscriptEvent event);
    public void noteRemoved(TranscriptEvent event);
}
