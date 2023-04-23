package dev.struchkov.godfather.telegram.simple.core;

import dev.struchkov.godfather.main.domain.content.ChatMail;
import dev.struchkov.godfather.simple.core.GeneralAutoResponder;
import dev.struchkov.godfather.simple.core.service.StorylineService;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public class ChatMailAutoresponderTelegram extends GeneralAutoResponder<ChatMail> {

    public ChatMailAutoresponderTelegram(
            StorylineService<ChatMail> storyLineService
    ) {
        super(storyLineService);
    }

}
