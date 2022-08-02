package dev.struchkov.godfather.telegram.quarkus.sender;

/**
 * // TODO: 18.09.2020 Добавить описание.
 *
 * @author upagge 18.09.2020
 */
@FunctionalInterface
public interface SendPreProcessing {

    String pretreatment(String messageText);

}
