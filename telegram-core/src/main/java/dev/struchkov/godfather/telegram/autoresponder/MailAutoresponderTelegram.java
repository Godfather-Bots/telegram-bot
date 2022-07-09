package dev.struchkov.godfather.telegram.autoresponder;

import dev.struchkov.godfather.context.domain.content.Mail;
import dev.struchkov.godfather.context.service.PersonSettingService;
import dev.struchkov.godfather.context.service.StorylineService;
import dev.struchkov.godfather.context.service.sender.Sending;
import dev.struchkov.godfather.core.GeneralAutoResponder;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public class MailAutoresponderTelegram extends GeneralAutoResponder<Mail> {

    public MailAutoresponderTelegram(
            Sending sending,
            PersonSettingService personSettingService,
            StorylineService<Mail> storyLineService
    ) {
        super(sending, personSettingService, storyLineService);
    }

}
