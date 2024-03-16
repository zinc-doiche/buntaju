package zinc.doiche.lib.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import zinc.doiche.jda

interface Command {
    val name: String
    val commandData: CommandData

    fun onCommand(event: SlashCommandInteractionEvent)
}