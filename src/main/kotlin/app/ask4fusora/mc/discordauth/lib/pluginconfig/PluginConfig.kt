package app.ask4fusora.mc.discordauth.lib.pluginconfig

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class PluginConfig<P : JavaPlugin>(
    private val plugin: P,
    private val fileName: String = "config.yml",
    private val prefix: String = ""
) {

    private val file = File(plugin.dataFolder, fileName)
    private var config: YamlConfiguration

    init {
        makeFile()
        config = load()
    }

    private fun makeFolder() {
        if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdirs()
    }

    private fun makeFile() {
        makeFolder()

        if (!file.exists()) {
            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false)
            } else {
                file.createNewFile()
            }
        }
    }

    private fun deleteFile() {
        if (!file.exists()) return

        if (file.delete())
            plugin.logger.warning("Deleted configuration file '$fileName'.")
        else
            plugin.logger.severe("Failed to delete config file '$fileName'!")
    }

    fun reset() {
        deleteFile()
        makeFile()
        load()
    }

    /**
     * Return an instance of the config.
     * Can be used to reload configuration.
     */
    fun load(): YamlConfiguration {
        config = YamlConfiguration.loadConfiguration(file)
        return config
    }

    protected inline fun <reified T> setting(default: T, group: String = "", path: String? = null): ConfigDelegate<T> {
        return createDelegate(default, group, path, T::class.java)
    }

    protected fun <T> createDelegate(
        default: T,
        group: String,
        path: String? = null,
        type: Class<T>
    ): ConfigDelegate<T> {
        if (path != null) require(path.isNotEmpty()) { "`path` can not be empty!" }
        return ConfigDelegate(prefix, group, path, default, type)
    }

    protected class ConfigDelegate<T>(
        private val prefix: String,
        private val group: String,
        private val path: String?,
        private val default: T,
        private val type: Class<T>
    ) : ReadWriteProperty<PluginConfig<*>, T> {

        override fun getValue(thisRef: PluginConfig<*>, property: KProperty<*>): T {
            val value = thisRef.config.get(getPath(path, property.name), default)
            return if (type.isInstance(value)) type.cast(value) else default
        }

        override fun setValue(thisRef: PluginConfig<*>, property: KProperty<*>, value: T) {
            thisRef.config.set(getPath(path, property.name), value)

            try {
                thisRef.config.save(thisRef.file)
            } catch (e: IOException) {
                thisRef.plugin.logger.severe("Could not save config file '${thisRef.fileName}'!")
                thisRef.plugin.logger.severe("Failed to save setting '$path'!")

                e.printStackTrace()
            }
        }

        private fun getPath(path: String?, propertyName: String): String {
            var path = path ?: propertyName.toKebabCase()

            if (group.isNotEmpty()) path = "$group.$path"
            if (prefix.isNotEmpty()) path = "$prefix.$path"

            return path
        }

        private fun String.toKebabCase(): String {
            val regex = Regex("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])")
            return this.replace(regex, "-").lowercase()
        }
    }
}