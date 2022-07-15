package dev.struchkov.godfather.telegram.domain.event;

import dev.struchkov.godfather.context.domain.event.Event;

public class Command implements Event {

    public static final String TYPE = "CMD";

    @Override
    public String getType() {
        return TYPE;
    }

}
