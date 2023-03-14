---
hide:
    - comments
    - navigation

---

# GodFather Bot - Фреймворк для создания Telegram ботов

Данный фреймворк обладает мощным API для создания диалоговых Telegram ботов.

!!! note ""

    Данный фреймворк имеет версии как для SpringBoot :simple-spring:, так и для Quarkus Reactive :simple-quarkus:. 

Посмотрите, как легко сделать Hello World:

``` java   
@Component
public class GeneralMenu implements UnitConfiguration {

    @Unit(value = GENERAL_MENU, main = true)
    public AnswerText<Mail> generalMenu() {
        return AnswerText.<Mail>builder()
                .answer(boxAnswer("Hello, World!"))
                .build();
    }

}
```

## Добавляем зависимости

Чтобы начать, выберете свой сборщик вкладку и добавьте зависимости в проект.

--8<-- ".dependencies.md"