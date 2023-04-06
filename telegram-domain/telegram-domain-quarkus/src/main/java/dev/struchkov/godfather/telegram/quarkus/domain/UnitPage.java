package dev.struchkov.godfather.telegram.quarkus.domain;

import dev.struchkov.godfather.main.domain.keyboard.KeyBoardButton;
import dev.struchkov.godfather.main.domain.keyboard.KeyBoardLine;
import dev.struchkov.godfather.quarkus.domain.BoxAnswer;
import dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard;
import dev.struchkov.haiti.utils.Inspector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static dev.struchkov.godfather.telegram.domain.UnitPaginationUtil.navigableLine;
import static dev.struchkov.godfather.telegram.domain.keyboard.SimpleKeyBoardLine.keyBoardLine;

public class UnitPage<T> {

    /**
     * Дополнительные линии клавиатуры. Выводятся после кнопок навигации.
     */
    private final List<KeyBoardLine> additionalLines = new ArrayList<>();

    /**
     * Элементы, которые будут выводиться.
     */
    private List<T> elements;

    /**
     * Номер текущей страницы.
     */
    private Integer currentOffset;

    /**
     * Общее количество элементов на всех страницах.
     */
    private Integer countAllElements;

    /**
     * Функция преобразования элементов с линии.
     */
    private Function<T, KeyBoardLine> function;

    /**
     * Сообщение, которое выводится над результатами
     */
    private String message;

    /**
     * Флаг, определяющий нужно ли заменить старое сообщение или вывести новое (по умолчанию заменять)
     */
    private boolean replace;

    /**
     * Флаг, исключающий добавление строки с навигацией
     */
    private boolean removeDefaultNavigableLine;

    /**
     * Сообщение, которое будет отправлено, если коллекция элементов пустая.
     */
    private BoxAnswer emptyElements;

    public static <T> UnitPage<T> builder() {
        return new UnitPage<>();
    }

    public UnitPage<T> elements(List<T> elements) {
        this.elements = elements;
        return this;
    }

    public UnitPage<T> countAllElements(Integer countAllElements) {
        this.countAllElements = countAllElements;
        return this;
    }

    public UnitPage<T> currentOffset(Integer currentOffset) {
        this.currentOffset = currentOffset;
        return this;
    }

    public UnitPage<T> additionLine(KeyBoardLine line) {
        additionalLines.add(line);
        return this;
    }

    public UnitPage<T> additionLine(KeyBoardButton button) {
        additionalLines.add(keyBoardLine(button));
        return this;
    }

    public UnitPage<T> mapper(Function<T, KeyBoardLine> function) {
        this.function = function;
        return this;
    }

    public UnitPage<T> message(String message) {
        this.message = message;
        return this;
    }

    public UnitPage<T> replace(boolean replace) {
        this.replace = replace;
        return this;
    }

    public UnitPage<T> emptyElements(BoxAnswer boxAnswer) {
        this.emptyElements = boxAnswer;
        return this;
    }

    public UnitPage<T> removeDefaultNavigableLine() {
        this.removeDefaultNavigableLine = true;
        return this;
    }

    public BoxAnswer build() {
        Inspector.isNotNull(currentOffset, countAllElements, function);
        if (elements.isEmpty()) {
            return emptyElements != null ? emptyElements : BoxAnswer.boxAnswer("Данные не найдены.");
        } else {
            final List<KeyBoardLine> lines = elements.stream()
                    .map(function)
                    .toList();
            final InlineKeyBoard.InlineKeyBoardBuilder builder = InlineKeyBoard.builder();

            lines.forEach(builder::line);
            if (!removeDefaultNavigableLine) {
                navigableLine(currentOffset, countAllElements).ifPresent(builder::line);
            }
            additionalLines.forEach(builder::line);

            final InlineKeyBoard keyBoard = builder.build();

            final BoxAnswer.Builder boxAnswer = BoxAnswer.builder()
                    .keyBoard(keyBoard)
                    .replace(true);
            if (message != null) {
                boxAnswer.message(message);
            }

            boxAnswer.replace(replace);
            return boxAnswer.build();
        }
    }

}
