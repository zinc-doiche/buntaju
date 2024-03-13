package zinc.doiche.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import zinc.doiche.jda

interface Command {
    val name: String
    val commandData: CommandData

    fun register() {
        jda.upsertCommand(commandData).queue()
        jda.addEventListener(this)
    }

    @SubscribeEvent
    fun onCommand(event: SlashCommandInteractionEvent)
}