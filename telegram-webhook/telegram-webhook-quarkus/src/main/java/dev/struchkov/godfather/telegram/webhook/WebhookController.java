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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Slf4j
@Path("callback")
public class WebhookController {

    private final String pathKey;
    private final EventDistributor eventDistributor;

    public WebhookController(TelegramBotConfig telegramBotConfig, EventDistributor eventDistributor) {
        this.eventDistributor = eventDistributor;
        this.pathKey = telegramBotConfig.getWebHookUrl().split("callback")[1];
    }

    @POST
    @Path("/{botPath}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateReceived(@PathParam("botPath") String botPath, Update update) {
        return Uni.createFrom().voidItem()
//                .onItem().invoke(() -> isTrue(pathKey.equals(botPath), accessException("В доступе отказано!")))
                .onItem().ignore().andSwitchTo(() -> eventDistributor.processing(update))
                .onItem().transform(ignore -> Response.ok().build());
    }

    @GET
    @Path("/{botPath}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> testReceived(@PathParam("botPath") String botPath) {
        return Uni.createFrom().voidItem()
//                .onItem().invoke(() -> isTrue(pathKey.equals(botPath), accessException("В доступе отказано!")))
                .onItem().transform(ignore -> "Hi there " + botPath + "!");
    }

}
