package com.justai.jaicf.template.scenario

import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.channel.yandexalice.alice
import com.justai.jaicf.channel.yandexalice.api.alice
import com.justai.jaicf.channel.yandexalice.model.AliceEvent
import com.justai.jaicf.channel.yandexalice.model.AliceIntent
import com.justai.jaicf.helpers.logging.log
import com.justai.jaicf.model.scenario.Scenario
import kotlin.math.log


object MainScenarioTest: Scenario(
    dependencies = listOf(GameScenario)
) {
    init {
        state("/main") {
            activators {
                regex("/start")
                event(AliceEvent.START)
            }

            action {
                reactions.say("Привет! Добро пожаловать в Норвежско-грузинский клуб русских песен!\n")
                reactions.say("Собрались однажды русский, профессор лингвистики, норвежец и грузин русские песни петь. Каждый на свой лад.\n")
                reactions.say("Предлагаю угадать исполнителя по некоторым строчкам, спетым на этом собрании.\n")
                reactions.say("Играем?")
                reactions.buttons("да", "нет")
            }

            state("/main/yes") {
                activators {
                    regex("да")
                    intent(AliceIntent.CONFIRM)
                }

                action {
                    reactions.go("/game")
                }
            }


            state("/main/no") {
                activators {
                    regex("нет")
                    intent(AliceIntent.REJECT)
                }

                action {
                    reactions.say("Окей. Пока!")
                    reactions.alice?.endSession()
                }
            }

            fallback {
                reactions.say("Я понимаю, что чего-то не понимаю. Скажи лучше, да или нет?")
            }
        }

        state("/end") {
            action {
                reactions.say("Кажется, мы всё с вами разыграли!\nПока!")
                reactions.telegram?.sendPhoto("https://meduza.io/image/attachments/images/005/634/857/large/7yf6EVsUAAsPGObrATeTHQ.jpg")
                reactions.alice?.endSession()
            }
        }


        fallback {
            reactions.telegram?.say("Я умею играть только в игру Норвежско-грузинский клуб русских песен. Заново сыграть можно отправив мне команду /start.")
        }
    }
}

