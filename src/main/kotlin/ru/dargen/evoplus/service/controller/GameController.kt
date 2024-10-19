package ru.dargen.evoplus.service.controller

import ru.dargen.rest.annotation.RequestHeader
import ru.dargen.rest.annotation.RequestMapping
import ru.dargen.rest.annotation.parameter.Parameter
import ru.dargen.rest.request.HttpMethod.POST

@RequestMapping("https://evo-plus.dargen.space")
interface GameController {

    @RequestMapping("/api/ingame/update", method = POST)
    fun update(@Parameter("username") username: String, @Parameter("version") version: String): Boolean

    @RequestMapping("/api/ingame/check")
    fun checkPlayer(@Parameter("username") username: String): Boolean

    @RequestMapping("POST /api/ingame/check/batch")
    @RequestHeader(key = "Accept", value = "application/json")
    @RequestHeader(key = "Content-Type", value = "application/json")
    fun checkPlayers(usernames: Collection<String>): Collection<String>

}