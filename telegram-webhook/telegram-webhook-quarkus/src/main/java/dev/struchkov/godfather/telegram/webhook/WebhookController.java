package dev.struchkov.godfather.telegram.webhook;

import dev.struchkov.godfather.telegram.domain.config.TelegramBotConfig;
import dev.struchkov.godfather.telegram.quarkus.context.service.EventDistributor;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static dev.struchkov.haiti.context.exception.AccessException.accessException;
import static dev.struchkov.haiti.utils.Inspector.isTrue;

@Slf4j
@Path("callback")
public class WebhookController {

    public static final String ERROR_ACCESS = "В доступе отказано!";
    private final String pathKey = "bot";
    private final String secretToken;
    private final EventDistributor eventDistributor;

    public WebhookController(TelegramBotConfig telegramBotConfig, EventDistributor eventDistributor) {
        this.secretToken = telegramBotConfig.getWebhookConfig().getSecretToken();
        this.eventDistributor = eventDistributor;
    }

    @POST
    @Path("{webhookPath}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateReceived(@PathParam("webhookPath") String botPath, @HeaderParam("X-Telegram-Bot-Api-Secret-Token") String secretTokenFromTelegram, Update update) {
        Uni.createFrom().voidItem()
                .invoke(() -> {
                    log.debug("Получено webhook событие");
                    isTrue(pathKey.equals(botPath), accessException(ERROR_ACCESS));
                    isTrue(secretToken.equals(secretTokenFromTelegram), accessException(ERROR_ACCESS));
                })
                .call(
                        () -> eventDistributor.processing(update)
                                .onFailure().recoverWithNull()
                )
                .invoke(() -> log.debug("Webhook событие успешно обработано"))
                .subscribe().with(
                        item -> {
                        },
                        failure -> log.error("Произошла ошибка: " + failure)
                );
        log.debug("Дали отмашку telegram, что webhook событие получено");
        return Response.ok().build();
    }

    @GET
    @Path("{webhookPath}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> testReceived(@PathParam("webhookPath") String botPath, @HeaderParam("X-Telegram-Bot-Api-Secret-Token") String secretTokenFromTelegram) {
        return Uni.createFrom().voidItem()
                .onItem().invoke(() -> {
                    isTrue(pathKey.equals(botPath), accessException(ERROR_ACCESS));
                    isTrue(secretToken.equals(secretTokenFromTelegram), accessException(ERROR_ACCESS));
                })
                .onItem().transform(ignore -> "Hi there " + botPath + "!");
    }

}
