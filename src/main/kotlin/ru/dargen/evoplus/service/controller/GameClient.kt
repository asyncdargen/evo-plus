package ru.dargen.evoplus.service.controller

import feign.Headers
import feign.Param
import feign.RequestLine

interface GameClient {

    @RequestLine("POST /api/ingame/update?username={username}&version={version}")
    fun update(@Param("username") username: String, @Param("version") version: String): Boolean

    @RequestLine("GET /api/ingame/check?username={username}")
    fun checkPlayer(@Param("username") username: String): Boolean

    @RequestLine("POST /api/ingame/check/batch")
    @Headers("Accept: application/json", "Content-Type: application/json")
    fun checkPlayers(usernames: Collection<String>): Collection<String>

}