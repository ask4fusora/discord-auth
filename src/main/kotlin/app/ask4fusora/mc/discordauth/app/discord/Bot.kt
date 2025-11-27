package app.ask4fusora.mc.discordauth.app.discord

import app.ask4fusora.mc.discordauth.DiscordAuth
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.requests.GatewayIntent
import java.util.logging.Logger

class Bot {

    companion object {

        @Volatile
        private var _instance: Bot? = null

        val instance: Bot
            get() = checkNotNull(_instance) { "Bot has not been initialized yet!" }
    }

    private val logger: Logger

    var jda: JDA
        private set

    init {
        if (_instance == null) _instance = this

        logger = DiscordAuth.instance.logger

        val config = DiscordAuth.Config.discord

        try {
            jda = JDABuilder.createLight(config.botToken)
                .setAutoReconnect(true)
                .enableIntents(GatewayIntent.entries)
                .build()
        } catch (e: InvalidTokenException) {
            throw Exception("Failed to login to Discord: ${e.message}")
        }
    }

    fun start() {
        try {
            jda.awaitReady()
        } catch (e: InterruptedException) {
            throw InterruptedException("Interrupted: ${e.message}")
        }
    }
}