package dev.struchkov.godfather.telegram.autoresponder;

import dev.struchkov.godfather.context.domain.content.Mail;
import dev.struchkov.godfather.context.service.PersonSettingService;
import dev.struchkov.godfather.context.service.UnitPointerService;
import dev.struchkov.godfather.context.service.sender.Sending;
import dev.struchkov.godfather.core.GeneralAutoResponder;

import java.util.List;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public class MessageAutoresponderTelegram extends GeneralAutoResponder<Mail> {

    public MessageAutoresponderTelegram(
            Sending sending,
            PersonSettingService personSettingService,
            UnitPointerService unitPointerService,
            List<Object> unitConfigurations
    ) {
        super(sending, personSettingService, unitPointerService, unitConfigurations);
    }

}
