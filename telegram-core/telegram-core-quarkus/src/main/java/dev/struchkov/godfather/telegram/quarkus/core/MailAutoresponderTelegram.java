package dev.struchkov.godfather.telegram.quarkus.core;

import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.quarkus.context.service.PersonSettingService;
import dev.struchkov.godfather.quarkus.core.GeneralAutoResponder;
import dev.struchkov.godfather.quarkus.core.service.StorylineService;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public class MailAutoresponderTelegram extends GeneralAutoResponder<Mail> {

    public MailAutoresponderTelegram(
            PersonSettingService personSettingService,
            StorylineService<Mail> storyLineService
    ) {
        super(personSettingService, storyLineService);
    }

}
