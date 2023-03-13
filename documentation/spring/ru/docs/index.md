---
hide:
    - comments

---

# Фреймворк для создания Telegram ботов

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

--8<-- ".dependencies.md"