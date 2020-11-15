package org.sadtech.bot.godfather.telegram.autoresponder;

import org.sadtech.autoresponder.repository.UnitPointerRepository;
import org.sadtech.social.bot.GeneralAutoResponder;
import org.sadtech.social.bot.domain.unit.MainUnit;
import org.sadtech.social.core.domain.content.Mail;
import org.sadtech.social.core.service.MessageService;
import org.sadtech.social.core.service.sender.Sending;

import java.util.Set;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public class MessageAutoresponderTelegram extends GeneralAutoResponder<Mail> {

    public MessageAutoresponderTelegram(
            Set<MainUnit> menuUnit, Sending sending,
            MessageService<Mail> messageService,
            UnitPointerRepository unitPointerRepository
    ) {
        super(menuUnit, sending, messageService, unitPointerRepository);
    }

}
