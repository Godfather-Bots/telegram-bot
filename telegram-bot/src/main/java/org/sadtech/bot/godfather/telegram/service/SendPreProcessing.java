package org.sadtech.bot.godfather.telegram.service;

/**
 * // TODO: 18.09.2020 Добавить описание.
 *
 * @author upagge 18.09.2020
 */
@FunctionalInterface
public interface SendPreProcessing {

    String pretreatment(String messageText);

}
