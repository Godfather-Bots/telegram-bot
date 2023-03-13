---
hide:
    - comments

---

# Фреймворк для создания Telegram ботов

Данный фреймворк обладает мощным API для создания диалоговых Telegram ботов.

## Добавляем зависимости

Данный фреймворк имеет версии как для SpringBoot :simple-spring:, так и для Quarkus Reactive :simple-quarkus:. 

Чтобы начать, выберете соответствующую вкладку и добавьте зависимости в свой проект.

=== ":simple-spring: SpringBoot"

    Используйте стартер, чтобы быстро начать разработку

    === ":simple-apachemaven: Maven"

        ``` xml
        <!-- https://mvnrepository.com/artifact/dev.struchkov.godfather.telegram/telegram-bot-spring-boot-starter -->
        <dependency>
            <groupId>dev.struchkov.godfather.telegram</groupId>
            <artifactId>telegram-bot-spring-boot-starter</artifactId>
            <version>0.0.51</version>
        </dependency>

        ```

    === ":simple-gradle: Gradle"

        ``` groovy
        // https://mvnrepository.com/artifact/dev.struchkov.godfather.telegram/telegram-bot-spring-boot-starter
        implementation 'dev.struchkov.godfather.telegram:telegram-bot-spring-boot-starter:0.0.51'
        ```

=== ":simple-quarkus: Quarkus Reactive"
    
    Реактивная версия

    !!! question "Почему нe extension?"

        Все будет, но не сразу :wink:
    
    === ":simple-apachemaven: Maven"

        ``` xml
        <!-- https://mvnrepository.com/artifact/dev.struchkov.godfather.telegram/telegram-consumer-quarkus -->
        <dependency>
            <groupId>dev.struchkov.godfather.telegram</groupId>
            <artifactId>telegram-consumer-quarkus</artifactId>
            <version>0.0.51</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/dev.struchkov.godfather.telegram/telegram-core-quarkus -->
        <dependency>
            <groupId>dev.struchkov.godfather.telegram</groupId>
            <artifactId>telegram-core-quarkus</artifactId>
            <version>0.0.51</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/dev.struchkov.godfather.telegram/telegram-sender-quarkus -->
        <dependency>
            <groupId>dev.struchkov.godfather.telegram</groupId>
            <artifactId>telegram-sender-quarkus</artifactId>
            <version>0.0.51</version>
        </dependency>
        ```

    === ":simple-gradle: Gradle"

        ``` groovy
        // https://mvnrepository.com/artifact/dev.struchkov.godfather.telegram/telegram-consumer-quarkus
        implementation 'dev.struchkov.godfather.telegram:telegram-consumer-quarkus:0.0.51'

        // https://mvnrepository.com/artifact/dev.struchkov.godfather.telegram/telegram-core-quarkus
        implementation 'dev.struchkov.godfather.telegram:telegram-core-quarkus:0.0.51'

        // https://mvnrepository.com/artifact/dev.struchkov.godfather.telegram/telegram-sender-quarkus
        implementation 'dev.struchkov.godfather.telegram:telegram-sender-quarkus:0.0.51'
        ```