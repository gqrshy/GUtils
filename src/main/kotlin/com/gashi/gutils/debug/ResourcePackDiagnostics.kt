package com.gashi.gutils.debug

import com.gashi.gutils.GUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

/**
 * Diagnostic utilities for troubleshooting resource pack loading issues
 *
 * Helps identify why server-provided sounds are not being registered
 */
object ResourcePackDiagnostics {

    /**
     * Check if sounds from a specific namespace are loaded
     *
     * @param namespace The namespace to check (e.g., "cobbleranked")
     * @return Number of sounds found in the registry for this namespace
     */
    fun checkNamespaceSounds(namespace: String): Int {
        var count = 0
        val soundIds = mutableListOf<String>()

        Registries.SOUND_EVENT.forEach { soundEvent ->
            val id = Registries.SOUND_EVENT.getId(soundEvent)
            if (id != null && id.namespace == namespace) {
                count++
                soundIds.add(id.toString())
            }
        }

        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")
        GUtils.LOGGER.info("  Resource Pack Diagnostics: Namespace '$namespace'")
        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")
        GUtils.LOGGER.info("Total sounds found: $count")

        if (count > 0) {
            GUtils.LOGGER.info("\nRegistered sound events:")
            soundIds.sorted().forEach { id ->
                GUtils.LOGGER.info("  - $id")
            }
        } else {
            GUtils.LOGGER.warn("\n⚠ No sounds found for namespace '$namespace'!")
            GUtils.LOGGER.warn("This indicates the resource pack is not loaded.")
        }

        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")

        return count
    }

    /**
     * Check the status of loaded resource packs
     */
    fun checkResourcePacks() {
        val client = MinecraftClient.getInstance()
        val resourceManager = client.resourceManager

        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")
        GUtils.LOGGER.info("  Resource Pack Status")
        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")

        val namespaces = resourceManager.allNamespaces
        GUtils.LOGGER.info("Loaded namespaces (${namespaces.size}):")
        namespaces.sorted().forEach { namespace ->
            GUtils.LOGGER.info("  - $namespace")
        }

        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")
    }

    /**
     * Attempt to find a specific sound file in resources
     *
     * @param soundId The sound identifier to search for
     * @return true if the sound file exists in resources
     */
    fun checkSoundFileExists(soundId: Identifier): Boolean {
        val client = MinecraftClient.getInstance()
        val resourceManager = client.resourceManager

        // Check sounds.json
        val soundsJsonPath = Identifier.of(soundId.namespace, "sounds.json")
        val hasSoundsJson = resourceManager.getResource(soundsJsonPath).isPresent

        // Check actual sound file
        val oggPath = Identifier.of(soundId.namespace, "sounds/${soundId.path}.ogg")
        val hasOggFile = resourceManager.getResource(oggPath).isPresent

        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")
        GUtils.LOGGER.info("  Sound File Check: $soundId")
        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")
        GUtils.LOGGER.info("sounds.json exists: $hasSoundsJson")
        GUtils.LOGGER.info("OGG file exists: $hasOggFile")
        GUtils.LOGGER.info("Expected path: $oggPath")
        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")

        return hasSoundsJson && hasOggFile
    }

    /**
     * Run full diagnostics on startup
     */
    fun runFullDiagnostics() {
        GUtils.LOGGER.info("\n\n")
        GUtils.LOGGER.info("╔═══════════════════════════════════════════════════════╗")
        GUtils.LOGGER.info("║  GUtils Resource Pack Diagnostics                    ║")
        GUtils.LOGGER.info("╚═══════════════════════════════════════════════════════╝")
        GUtils.LOGGER.info("")

        // Check resource packs
        checkResourcePacks()

        // Check for cobbleranked sounds
        val cobblerankedSoundCount = checkNamespaceSounds("cobbleranked")

        // Provide recommendations
        GUtils.LOGGER.info("\n")
        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")
        GUtils.LOGGER.info("  Recommendations")
        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")

        if (cobblerankedSoundCount == 0) {
            GUtils.LOGGER.warn("⚠ Server resource pack not loaded!")
            GUtils.LOGGER.warn("")
            GUtils.LOGGER.warn("Possible causes:")
            GUtils.LOGGER.warn("  1. Server resource pack not configured in server.properties")
            GUtils.LOGGER.warn("  2. Client declined the resource pack prompt")
            GUtils.LOGGER.warn("  3. Resource pack download failed")
            GUtils.LOGGER.warn("  4. Resource pack applied but sounds.json is missing/invalid")
            GUtils.LOGGER.warn("")
            GUtils.LOGGER.warn("Solutions:")
            GUtils.LOGGER.warn("  1. Check server.properties has resource-pack URL")
            GUtils.LOGGER.warn("  2. Accept the resource pack when prompted on join")
            GUtils.LOGGER.warn("  3. Check client logs for resource pack download errors")
            GUtils.LOGGER.warn("  4. Try reconnecting to the server")
        } else {
            GUtils.LOGGER.info("✓ Server resource pack loaded successfully!")
            GUtils.LOGGER.info("  Found $cobblerankedSoundCount custom sounds")
        }

        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")
        GUtils.LOGGER.info("\n\n")
    }
}
