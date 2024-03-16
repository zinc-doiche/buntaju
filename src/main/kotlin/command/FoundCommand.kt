package zinc.doiche.command

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.commands.build.Commands
import zinc.doiche.chat.`object`.Channel
import zinc.doiche.chat.`object`.Server
import zinc.doiche.lib.command.CommandFactory

internal fun foundCommand() = CommandFactory.create(
    "분타설립",
    Commands.slash("분타설립", "분타주가 해당 채널에서 활동할 수 있게 해요.")
) { event ->
    val guild = event.guild ?: return@create

    event.deferReply().queue()

    runBlocking {
        async {
            Server.findById(guild.idLong) ?: Server.save(guild)
            val textChannel = event.channel as? TextChannel ?: run {
                return@async "텍스트 채널에서만 사용할 수 있어요."
            }
            Channel.findById(textChannel.idLong)?.let {
                return@async "'${textChannel.name}' 채널은 이미 등록되어 있어요."
            } ?: Channel.save(textChannel)

            "'${textChannel.name}' 채널이 등록되었습니다."
        }.await().let { message ->
            event.hook.sendMessage(message).queue()
        }
    }
}
