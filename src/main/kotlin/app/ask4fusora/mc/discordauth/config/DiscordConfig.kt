package app.ask4fusora.mc.discordauth.config

import app.ask4fusora.mc.discordauth.DiscordAuth
import app.ask4fusora.mc.discordauth.lib.pluginconfig.PluginConfig

class DiscordConfig(plugin: DiscordAuth) : PluginConfig<DiscordAuth>(plugin = plugin, prefix = "discord") {
    var botToken by setting("YOUR_BOT_TOKEN_HERE")
    var guildId by setting("YOUR_SERVER_ID_HERE")
}