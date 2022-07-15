package dev.struchkov.godfather.telegram.service.provider;

import dev.struchkov.godfather.context.service.EventProvider;
import dev.struchkov.godfather.telegram.domain.event.Command;

public class CommandProvider implements EventProvider<Command> {

    @Override
    public void sendEvent(Command event) {

    }

    @Override
    public String getEventType() {
        return Command.TYPE;
    }

}
