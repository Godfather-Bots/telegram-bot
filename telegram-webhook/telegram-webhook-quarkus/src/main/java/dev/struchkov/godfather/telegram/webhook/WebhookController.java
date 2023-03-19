package dev.struchkov.godfather.telegram.webhook;

import dev.struchkov.godfather.telegram.domain.config.TelegramBotConfig;
import dev.struchkov.godfather.telegram.quarkus.context.service.EventDistributor;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static dev.struchkov.haiti.context.exception.AccessException.accessException;
import static dev.struchkov.haiti.utils.Inspector.isTrue;

@Slf4j
public class WebhookController {

    public static final String ERROR_ACCESS = "В доступе отказано!";
    private final String pathKey;
    private final String accessKey;
    private final EventDistributor eventDistributor;

    public WebhookController(TelegramBotConfig telegramBotConfig, EventDistributor eventDistributor) {
        this.accessKey = telegramBotConfig.getWebhookConfig().getAccessKey();
        this.eventDistributor = eventDistributor;
        this.pathKey = telegramBotConfig.getWebhookConfig().getPath();
    }

    @POST
    @Path("{webhookPath}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateReceived(@PathParam("webhookPath") String botPath, @QueryParam("webhookAccessKey") String webhookAccessKey, Update update) {
        return Uni.createFrom().voidItem()
                .invoke(() -> log.trace("Получено webhook событие"))
                .invoke(() -> {
                    isTrue(pathKey.equals(botPath), accessException(ERROR_ACCESS));
                    isTrue(accessKey.equals(webhookAccessKey), accessException(ERROR_ACCESS));
                })
                .onItem().ignore().andSwitchTo(() -> eventDistributor.processing(update))
                .invoke(() -> log.trace("Webhook событие успешно обработано"))
                .replaceWith(Response.ok().build());
    }

    @GET
    @Path("{webhookPath}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> testReceived(@PathParam("webhookPath") String botPath, @QueryParam("webhookAccessKey") String webhookAccessKey) {
        return Uni.createFrom().voidItem()
                .onItem().invoke(() -> {
                    isTrue(pathKey.equals(botPath), accessException(ERROR_ACCESS));
                    isTrue(accessKey.equals(webhookAccessKey), accessException(ERROR_ACCESS));
                })
                .onItem().transform(ignore -> "Hi there " + botPath + "!");
    }

}
