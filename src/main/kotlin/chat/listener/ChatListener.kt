package zinc.doiche.chat.listener

import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import zinc.doiche.chat.`object`.Channel

class ChatListener {

    @SubscribeEvent
    fun onMessageReceived(event: MessageReceivedEvent) = runBlocking {
        if(event.author.isBot) {
            return@runBlocking
        }
        val guild = event.guild
        val user = event.author
        val textChannel = event.channel as? TextChannel ?: return@runBlocking
        val message = event.message
        val channel = Channel.findById(textChannel.idLong) ?: return@runBlocking

        textChannel.sendMessage("Hello, ${user.name}!").queue()
    }
}