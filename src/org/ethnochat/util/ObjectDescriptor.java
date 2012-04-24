package org.ethnochat.util;

public class ObjectDescriptor<T extends NamedObject> {

    private T object;

    public ObjectDescriptor(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    @Override public String toString() {
        return object.getName();
    }
}
