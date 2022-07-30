package dev.struchkov.godfather.telegram.domain.event;

import dev.struchkov.godfather.context.domain.event.Event;

import java.util.Optional;

public class Command implements Event {

    public static final String TYPE = "CMD";

    private String value;
    private String commandType;
    private String arg;
    private String rawValue;

    private String firstName;
    private String lastName;
    private Long personId;

    public void setValue(String value) {
        this.value = value;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getValue() {
        return value;
    }

    public Optional<String> getArg() {
        return Optional.ofNullable(arg);
    }

    public String getCommandType() {
        return commandType;
    }

    public String getRawValue() {
        return rawValue;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getPersonId() {
        return personId;
    }

    @Override
    public String getEventType() {
        return TYPE;
    }
}
