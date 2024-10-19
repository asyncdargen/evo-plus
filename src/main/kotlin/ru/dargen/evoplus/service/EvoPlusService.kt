package ru.dargen.evoplus.service

import ru.dargen.evoplus.Logger
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.service.controller.GameController
import ru.dargen.evoplus.util.Updater
import ru.dargen.evoplus.util.collection.takeIfNotEmpty
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.newSetCacheExpireAfterWrite
import ru.dargen.rest.RestClientFactory
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.time.Duration.Companion.minutes

object EvoPlusService {

    private val GameClient = RestClientFactory.createHttpBuiltinClient()
        .createController<GameController>(GameController::class.java)

    private val ingamePlayers = newSetCacheExpireAfterWrite<String>(1.minutes)

    init {
        scheduleEvery(period = 30, unit = SECONDS) {
            runCatching { GameClient.update(Client.session.username, Updater.ModVersion) }
                .onFailure { Logger.error("Error while updating ingame status", it) }
                .onSuccess { if (!it) Logger.warn("Failed ingame status update") }
        }

        scheduleEvery(20, 20, unit = SECONDS) { fetchIngamePlayers() }
    }

    fun isIngame(username: String) = username.lowercase() in ingamePlayers

    private fun fetchIngamePlayers(
        players: Collection<String> = Client.networkHandler?.playerList
            ?.mapNotNull { it?.profile?.name }
            ?: emptySet(),
    ) {

        val players = players
            .filterNot { '§' in it || it.isBlank() }
            .filter { !isIngame(it).apply { if (this) ingamePlayers.add(it) } }
            .map(String::lowercase)
            .takeIfNotEmpty() ?: return

        runCatching { GameClient.checkPlayers(players) }
            .onFailure { Logger.error("Error while fetch ingame players", it) }
            .onSuccess {
                ingamePlayers.addAll(it)
                ingamePlayers.add(Client.session.username.lowercase())
                Logger.info("${it.size}/${players.size} with EvoPlus!")
            }
    }

}