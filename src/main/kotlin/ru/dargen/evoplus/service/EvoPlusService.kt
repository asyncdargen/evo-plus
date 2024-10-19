package ru.dargen.evoplus.service

import ru.dargen.evoplus.Logger
import ru.dargen.evoplus.api.event.evo.ChangeLocationEvent
import ru.dargen.evoplus.api.event.evo.EvoJoinEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.scheduler.async
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.service.controller.GameController
import ru.dargen.evoplus.util.Updater
import ru.dargen.evoplus.util.collection.takeIfNotEmpty
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.newSetCacheExpireAfterWrite
import ru.dargen.rest.RestClientFactory
import java.util.concurrent.TimeUnit.SECONDS
import java.util.logging.Level
import kotlin.time.Duration.Companion.minutes

object EvoPlusService {

    private val GameClient = RestClientFactory.createHttpBuiltinClient()
        .createController<GameController>(GameController::class.java)

    private val ingamePlayers = newSetCacheExpireAfterWrite<String>(1.minutes)

    init {
        scheduleEvery(period = 30, unit = SECONDS) {
            runCatching {
                GameClient.update(Client.session.username, Updater.ModVersion)
            }.onFailure {
                Logger.log(Level.SEVERE, "Error while updating ingame status", it)
            }.onSuccess {
                if (!it) {
                    Logger.warning("Failed ingame status update")
                }
            }
        }

        scheduleEvery(10, 10, unit = SECONDS) { fetchIngamePlayers() }
        on<ChangeLocationEvent> {async { fetchIngamePlayers() } }
        on<EvoJoinEvent> { async { fetchIngamePlayers() }}
    }

    fun isIngame(username: String) = username.lowercase() in ingamePlayers

    private fun fetchIngamePlayers(
        players: Collection<String> = Client.networkHandler
            ?.playerList
            ?.mapNotNull { it?.profile?.name }
            ?.map(String::lowercase)
            ?: emptySet(),
    ) {
        players.takeIfNotEmpty() ?: return
        runCatching {
            GameClient.checkPlayers(players)
        }.onFailure {
            Logger.log(Level.SEVERE, "Error while fetch ingame players", it)
        }.onSuccess {
            ingamePlayers.addAll(it)
            Logger.info("${it.size}/${players.size} with EvoPlus!")
        }
    }

}