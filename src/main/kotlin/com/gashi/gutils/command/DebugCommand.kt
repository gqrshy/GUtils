package com.gashi.gutils.command

import com.gashi.gutils.debug.ResourcePackDiagnostics
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text

/**
 * Client-side debug commands for GUtils
 *
 * Usage:
 * - /gutilsdebug - Run full resource pack diagnostics
 */
object DebugCommand {

    /**
     * Register debug commands
     */
    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            ClientCommandManager.literal("gutilsdebug")
                .executes(::executeDebug)
        )
    }

    /**
     * Execute debug diagnostics
     */
    private fun executeDebug(context: CommandContext<FabricClientCommandSource>): Int {
        val source = context.source

        try {
            source.sendFeedback(Text.literal("§6[GUtils] Running resource pack diagnostics..."))
            source.sendFeedback(Text.literal("§7Check logs for detailed results."))

            // Run diagnostics
            ResourcePackDiagnostics.runFullDiagnostics()

            source.sendFeedback(Text.literal("§a[GUtils] Diagnostics complete! Check logs for details."))

            return 1
        } catch (e: Exception) {
            source.sendError(Text.literal("§c[GUtils] Failed to run diagnostics: ${e.message}"))
            return 0
        }
    }
}
