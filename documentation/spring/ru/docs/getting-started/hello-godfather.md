# Hello GodFather

Начнем разработку бота с простого примера. Поздороваемся с пользователем.

## Регистрация бота

Сначала нужно зарегистрировать бота и получить токен в Telegram.

[:robot: Зарегистрировать бота](@BotFather){ .md-button }


## Зависимости

--8<-- ".dependencies.md"

## Конфигурация

Теперь необходимо указать данные для подключения к боту в Telegram.

``` yaml title="application.yml"
telegram:
  bot:
    username: username_bot
    token: your_token
```

### Прокси
Если телеграм заблокирован у вашего хостера/провайдера, вы можете использовать прокси

``` yaml title="application.yml" hl_lines="5-11"
telegram:
  bot:
    username: username_bot
    token: your_token
  proxy:
    enable: true
    host: PROXY_HOST
    port: PROXY_PORT
    type: PROXY_TYPE
    user: PROXY_USERNAME
    password: PROXY_PASSWORD
```

## Первый юнит

Теперь создаем класс конфигурации юнитов и добавляем первый юнит (1).
{ .annotate }

1. Юниты это базовая сущность фреймворка

``` java   
@Component
public class GeneralMenu implements UnitConfiguration {

    @Unit(value = GENERAL_MENU, main = true)
    public AnswerText<Mail> generalMenu() {
        return AnswerText.<Mail>builder()
                .answer(boxAnswer("Hello!"))
                .build();
    }

}
```

Вот и все, можете запустить ваше приложение и написать боту в телеграм. Если вы все сделали правильно, то он ответит вам.