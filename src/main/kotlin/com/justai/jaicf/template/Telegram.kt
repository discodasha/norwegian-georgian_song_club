import com.justai.jaicf.channel.telegram.TelegramChannel
import com.justai.jaicf.template.templateBot

fun main() {
    TelegramChannel(templateBot, "").run()
}