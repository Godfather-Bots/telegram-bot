package dev.struchkov.godfather.telegram.autoresponder;

import dev.struchkov.godfather.context.domain.content.Mail;
import dev.struchkov.godfather.context.service.MessageService;
import dev.struchkov.godfather.context.service.sender.Sending;
import dev.struchkov.godfather.core.GeneralAutoResponder;
import dev.struchkov.godfather.core.domain.unit.MainUnit;
import org.sadtech.autoresponder.repository.UnitPointerRepository;

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
