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
        state("main") {
            activators {
                regex("/start")
                event(AliceEvent.START)
            }

            action {
                reactions.say("Привет! Добро пожаловать в Норвежско-грузинский клуб русских песен!\n")
                reactions.say("Собрались однажды русский, профессор лингвистики, норвежец и грузин русские песни петь. Каждый на свой лад.\n")
                reactions.say("Вы - почетный гость на этом событии. И ваша задача - угадать исполнителя по некоторым строчкам, спетым на этом собрании.\n")
                reactions.say("Играем?")
                reactions.buttons("Да", "Нет")
            }

            state("yes") {
                activators {
                    regex("да")
                    regex("не *")
                    intent(AliceIntent.CONFIRM)
                }

                action {
                    reactions.go("/game")
                }
            }


            state("no") {
                activators {
                    regex("нет")
                    intent(AliceIntent.REJECT)
                }

                action {
                    reactions.say("Окей. Пока!")
                    reactions.alice?.endSession()
                }
            }

            fallback("f3") {
                reactions.say("Я понимаю, что чего-то не понимаю. Скажи лучше, играем ли: да или нет?")
            }
        }

        state("/end") {
            activators {
                regex("хватит")
                regex("стоп")
            }

            action {
                if (context.client["score"] != null)
                    reactions.say("Вы отгадали " + context.client["score"].toString()
                        + " из " + GameScenario.game.tasksArray.size.toString() + " песен.")
                reactions.say("Интернациональная команда надеется на ваше скорое возвращение.")
                reactions.say("До свидания!\n")
                reactions.telegram?.sendPhoto("https://meduza.io/image/attachments/images/005/634/857/large/7yf6EVsUAAsPGObrATeTHQ.jpg")
                reactions.alice?.endSession()
            }
        }


        fallback("f2") {
            reactions.telegram?.say("Я умею играть только в игру Норвежско-грузинский клуб русских песен. Заново сыграть можно отправив мне команду /start.")
        }
    }
}

