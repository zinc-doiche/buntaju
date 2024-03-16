package zinc.doiche.lib.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import zinc.doiche.jda
import zinc.doiche.logger

class CommandFactory {

    @SubscribeEvent
    fun onCommand(event: SlashCommandInteractionEvent) {
        if(event.name in commands) {
            commands[event.name]?.onCommand(event)
        }
    }

    companion object {
        private val commands = HashMap<String, Command>()

        fun register(command: Command) {
            jda.upsertCommand(command.commandData).queue()
            commands[command.name] = command
        }

        fun create(
            name: String,
            commandData: CommandData,
            onCommand: (SlashCommandInteractionEvent) -> Unit
        ) = object: Command {
            override val name = name
            override val commandData = commandData

            override fun onCommand(event: SlashCommandInteractionEvent) {
                onCommand(event)
            }
        }
    }
}

internal fun Command.register() {
    CommandFactory.register(this)
}