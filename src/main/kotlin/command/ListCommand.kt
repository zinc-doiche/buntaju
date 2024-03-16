package zinc.doiche.command

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.commands.build.Commands
import zinc.doiche.chat.`object`.Channel
import zinc.doiche.lib.database.eq
import zinc.doiche.lib.command.CommandFactory

fun listCommand() = CommandFactory.create(
    "분타목록",
    Commands.slash("분타목록", "분타로 등록된 채널 목록을 보여줘요.")
) { event ->
    val guild = event.guild ?: return@create
    val guildId = guild.idLong

    event.deferReply().queue()

    runBlocking {
        async {
            EmbedBuilder()
                .setAuthor("개방 분타주")
                .setTitle("분타 목록")
                .apply {
                    Channel.collection.find(Channel::guildId eq guildId).collect {
                        addField(it.name, "", false)
                    }
                    setDescription("${guild.name}지부의 분타로 등록된 채널은 ${fields.size}개 입니다.")
                }.build()
        }.await().let { embed ->
            event.hook.sendMessageEmbeds(embed).queue()
        }
    }
}