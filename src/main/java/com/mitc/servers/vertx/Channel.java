package com.mitc.servers.vertx;

public enum Channel {

    BO_READ_CHANNEL("boReadChannel"),
    BO_WRITE_CHANNEL("boWriteChannel");

    private final String name;

    private Channel(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}