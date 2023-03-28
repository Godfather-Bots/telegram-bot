package dev.struchkov.godfather.telegram.domain;

import dev.struchkov.godfather.main.domain.keyboard.KeyBoardButton;
import dev.struchkov.godfather.main.domain.keyboard.KeyBoardLine;
import dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;


public final class UnitPaginationUtil {

    public static final String PAGE_REGEX = "^page\\s\\d*$";

    private static final String PAGINATION_SCHEMA = "page %s";
    private static final Pattern PATTERN_FOR_SEARCH_OFFSET = Pattern.compile("\\d++$");

    /**
     * Создание строки пагинации для передачи ее в payload кнопки
     */
    public static String getStringForOffset(Integer offset) {
        return PAGINATION_SCHEMA.formatted(offset);
    }

    /**
     * Разбор передаваемой строки пагинации
     */
    public static Optional<Integer> getOffsetFromString(String offsetString) {
        if (!Pattern.matches(PAGE_REGEX, offsetString)) {
            return Optional.empty();
        }
        final Matcher matcher = PATTERN_FOR_SEARCH_OFFSET.matcher(offsetString);
        if (matcher.find()) {
            final String offsetNumber = offsetString.substring(matcher.start(), matcher.end());
            return Optional.of(Integer.valueOf(offsetNumber));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Получение строки для клавиатуры, в которой содержатся кнопки навигации.
     *
     * @param currentOffset - текущее смещение, используемое для запроса
     * @param countElements - общее количество элементов которое может вернуть запрос
     */
    public static Optional<KeyBoardLine> navigableLine(Integer currentOffset, Integer countElements) {
        final SimpleKeyBoardLine.SimpleKeyBoardLineBuilder lineBuilder = SimpleKeyBoardLine.builder();

        final Optional<KeyBoardButton> optPrevButton = getPrevPageButton(currentOffset);
        final Optional<KeyBoardButton> optNextButton = getNextPageButton(currentOffset, countElements);

        if (optPrevButton.isPresent() || optNextButton.isPresent()) {
            optPrevButton.ifPresent(lineBuilder::button);
            optNextButton.ifPresent(lineBuilder::button);
            return Optional.of(lineBuilder.build());
        }
        return Optional.empty();
    }

    /**
     * Получение кнопки навигации на прошлую страницу, т.к. пользователь находится на самой первой странице, то
     * кнопки перехода на предыдущую страницу может и не быть
     */
    private static Optional<KeyBoardButton> getPrevPageButton(Integer currentOffset) {
        if (!currentOffset.equals(0)) {
            int prevOffset = currentOffset < 5 ? 0 : currentOffset - 5;
            return Optional.of(simpleButton("❮❮❮", getStringForOffset(prevOffset)));
        }
        return Optional.empty();
    }

    /**
     * Получение кнопки навигации на следующую страницу, т.к. пользователь может находиться на последней странице,
     * то кнопки навигации вперед может и не быть
     */
    private static Optional<KeyBoardButton> getNextPageButton(Integer currentOffset, Integer countElements) {
        final Integer nextOffset = currentOffset + 5;
        if (nextOffset.compareTo(countElements) < 0) {
            return Optional.of(simpleButton("❯❯❯", getStringForOffset(nextOffset)));
        }
        return Optional.empty();
    }

}