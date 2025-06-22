package ru.joutak.blockHandler

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class BlockHandler : JavaPlugin(), Listener {
    companion object {
        @JvmStatic
        lateinit var instance: BlockHandler
    }

    // Поменять блок
    // private val CHECK_BLOCK = Material.IRON_BLOCK;
    private val CHECK_BLOCK = Material.DIAMOND_BLOCK;
    private var customConfig = YamlConfiguration()

    private fun loadConfig() {
        val fx = File(dataFolder, "config.yml")
        if (!fx.exists()) {
            saveResource("config.yml", true)
        }
    }

    /**
     * Plugin startup logic
     */
    override fun onEnable() {
        instance = this

        loadConfig()

        // Register commands and events
        server.pluginManager.registerEvents(this, this)
        logger.info("=========================")
        logger.info("Плагин ${pluginMeta.name} версии ${pluginMeta.version} включен!")
        logger.info("=========================")
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.block.type == CHECK_BLOCK) {
            var player = event.player.name;

            var block = event.block.type;

            val currentTime = LocalTime.now()
                .format(DateTimeFormatter
                    .ofPattern("HH:mm:ss"))

            val raw = "${currentTime}: ${block} установлен игроком с ником ${player}! " +
                    "(Версия: ${pluginMeta.version}"

            val component: Component = LegacyComponentSerializer.legacySection().deserialize(raw)

            server.broadcast(component)
        }
    }

    /**
     * Plugin shutdown logic
     */
    override fun onDisable() {
    }
}
