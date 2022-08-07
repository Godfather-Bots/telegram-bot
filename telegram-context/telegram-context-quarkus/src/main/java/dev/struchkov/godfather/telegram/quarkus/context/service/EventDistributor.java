package dev.struchkov.godfather.telegram.quarkus.context.service;

import io.smallrye.mutiny.Uni;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.constraints.NotNull;

public interface EventDistributor {

    Uni<Void> processing(@NotNull Update update);

}

