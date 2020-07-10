package com.justai.jaicf.template.scenario

import com.justai.jaicf.channel.yandexalice.alice
import com.justai.jaicf.channel.yandexalice.model.AliceEvent
import com.justai.jaicf.model.scenario.Scenario

object MainScenario: Scenario() {
    init {
        state("main") {
            activators {
                event(AliceEvent.START)
            }

            action {
                reactions.say("Майор на связи. Докладывайте.")
                reactions.alice?.image(
                    url = "https://i.imgur.com/YOnWzLM.jpg",
                    title = "Майор на связи",
                    description = "Начните сообщение со слова \"Докладываю\"")
            }
        }

        state("report") {
            activators {
                regex("докладываю .+")
            }

            action {
                reactions.run {
                    say("Спасибо.")
                    sayRandom(
                        "Ваш донос зарегистрирован под номером ${random(1000, 9000)}.",
                        "Оставайтесь на месте. Не трогайте вещественные доказательства."
                    )
                    say("У вас есть еще какая-нибудь информация?")
                    buttons("Да", "Нет")
                }
            }

            state("yes") {
                activators {
                    regex("да")
                }

                action {
                    reactions.say("Докладывайте.")
                }
            }

            state("no") {
                activators {
                    regex("нет")
                    regex("отбой")
                }

                action {
                    reactions.sayRandom("Отбой.", "До связи.")
                    reactions.alice?.endSession()
                }
            }

        }
    }
}