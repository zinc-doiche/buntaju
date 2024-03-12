package zinc.doiche.chat.listener

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent

class ChatListener {

    @SubscribeEvent
    fun onMessageReceived(event: MessageReceivedEvent) {
        event.guild

        val user = event.author
    }

}