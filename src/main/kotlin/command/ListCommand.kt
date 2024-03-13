package zinc.doiche.command

import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import zinc.doiche.chat.`object`.Channel
import zinc.doiche.database.eq

class ListCommand: Command {
    override val name: String = "분타목록"
    override val commandData: CommandData = Commands.slash(name, "분타로 등록된 채널 목록을 보여줘요.")

    override fun onCommand(event: SlashCommandInteractionEvent) = runBlocking {
        if (event.name != name) {
            return@runBlocking
        }
        val guild = event.guild ?: return@runBlocking
        val guildId = guild.idLong
        EmbedBuilder()
            .setAuthor("개방 분타주")
            .setTitle("분타 목록")
            .apply {
                Channel.collection.find(Channel::guildId eq guildId).collect {
                    addField(it.name, "", false)
                }
                setDescription("${guild.name}지부의 분타로 등록된 채널은 ${fields.size}개 입니다.")
                event.deferReply().queue { hook ->
                    hook.sendMessageEmbeds(build())
                }
            }
    }
}