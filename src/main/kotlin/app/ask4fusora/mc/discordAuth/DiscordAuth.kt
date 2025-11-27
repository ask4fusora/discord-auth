package app.ask4fusora.mc.discordAuth

import org.bukkit.plugin.java.JavaPlugin

class DiscordAuth : JavaPlugin() {

    override fun onEnable() {
        this.logger.info("Plugin started")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
