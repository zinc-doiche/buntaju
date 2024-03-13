package zinc.doiche.command

import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands
import zinc.doiche.chat.`object`.Channel
import zinc.doiche.chat.`object`.Server

class FoundCommand: Command {
    override val name: String = "분타설립"
    override val commandData = Commands.slash(name, "분타주가 해당 채널에서 활동할 수 있게 해요.")

    override fun onCommand(event: SlashCommandInteractionEvent) = runBlocking {
        if (event.name != name) {
            return@runBlocking
        }
        val guild = event.guild ?: return@runBlocking
        Server.findById(guild.idLong) ?: Server.save(guild)

        val textChannel = event.channel as? TextChannel ?: run {
            event.deferReply().queue { hook ->
                hook.sendMessage("텍스트 채널에서만 사용할 수 있어요.").queue()
            }
            return@runBlocking
        }

        Channel.findById(textChannel.idLong)?.let {
            event.deferReply().queue { hook ->
                hook.sendMessage("'${textChannel.name}' 채널은 이미 등록되어 있어요.").queue()
            }
            return@runBlocking
        } ?: Channel.save(textChannel)

        event.deferReply().queue { hook ->
            hook.sendMessage("'${textChannel.name}' 채널이 등록되었습니다.").queue()
        }
    }
}