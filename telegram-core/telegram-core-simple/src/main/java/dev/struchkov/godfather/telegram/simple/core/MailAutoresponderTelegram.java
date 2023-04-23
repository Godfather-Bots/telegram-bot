package dev.struchkov.godfather.telegram.simple.core;

import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.core.GeneralAutoResponder;
import dev.struchkov.godfather.simple.core.service.StorylineService;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public class MailAutoresponderTelegram extends GeneralAutoResponder<Mail> {

    public MailAutoresponderTelegram(
            StorylineService<Mail> storyLineService
    ) {
        super(storyLineService);
    }

}
