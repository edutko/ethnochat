package org.ethnochat.messaging;

import java.util.UUID;

public class DummyMessagingService
        extends DefaultMessagingService {

    private UUID uuid;

    public DummyMessagingService(UUID id, String name) {
        uuid = id;
        setName(name);
    }

    @Override public UUID getID() {
        return uuid;
    }
}
