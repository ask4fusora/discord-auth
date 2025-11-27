package app.ask4fusora.mc.discordauth

import app.ask4fusora.mc.discordauth.app.discord.Bot
import app.ask4fusora.mc.discordauth.config.DiscordConfig
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class DiscordAuth : JavaPlugin() {

    companion object {

        @Volatile
        private var _instance: DiscordAuth? = null

        val instance: DiscordAuth
            get() = checkNotNull(_instance) { "Plugin has not been initialized yet!" }
    }

    object Config {
        lateinit var discord: DiscordConfig
    }

    private fun setupConfig() {
        Config.discord = DiscordConfig(this)
    }

    private fun setupBot() {
        Bot().start()
    }

    override fun onEnable() {
        try {
            if (_instance == null) _instance = this

            setupConfig()
            setupBot()
        } catch (e: Exception) {
            logger.log(Level.SEVERE, e.message, e)
            server.shutdown()
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
