package com.justai.jaicf.template.scenario

import com.justai.jaicf.channel.yandexalice.model.AliceEvent
import com.justai.jaicf.channel.yandexalice.model.AliceIntent
import com.justai.jaicf.context.BotContext
import com.justai.jaicf.helpers.logging.log
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.template.model.GameModel
import com.justai.jaicf.template.model.Hint
import com.justai.jaicf.template.model.Task
import com.justai.jaicf.template.model.getQuestion

object GameScenario: Scenario() {

    private val game = GameModel()
    val INIT_STATE = "/game/init"
    val GO_ON_STATE = "/game/go_on"

    init {
        state("/game") {
            action {
                reactions.say("Начинаем играть!\n")
                reactions.go(INIT_STATE)
                context.client["score"] = 0
            }
        }

        state("/game/init") {
            action {
                val task: Task? = game.getTask()
                if (task == null) {
                    reactions.go("/end")
                } else {
                    reactions.say(getQuestion(task))
                    val hint = Hint(task)
                    context.client["stage"] = 0
                    context.client["hint"] = hint
                    reactions.go("/game/ask")
                }

            }
        }

        state("/game/ask") {
            action {
                val index: Int = context.client["stage"] as Int
                val hint = context.client["hint"] as Hint
                if (index == hint.candidates.size) {
                    reactions.go(INIT_STATE)
                }
                reactions.say("Это " + hint.candidates[index] + "?")
            }
        }

        state("/game/hint_no") {
            activators {
                regex("нет")
                intent(AliceIntent.REJECT)
            }
            action {
                val stage: Int = context.client["stage"] as Int
                val hint = context.client["hint"] as Hint
                if (hint.rightIndex == stage) {
                    reactions.sayRandom("А вот и нет! Это " + hint.author)
                    reactions.go(INIT_STATE)
                } else {
                    reactions.go(GO_ON_STATE)
                }
            }
        }


        state("/game/hint_yes") {
            activators {
                regex("да")
                intent(AliceIntent.CONFIRM)
            }
            action {
                val stage: Int = context.client["stage"] as Int
                val hint = context.client["hint"] as Hint
                if (hint.rightIndex == stage) {
                    reactions.say("Правильно!")
                    reactions.say(hint.original)
                    reactions.go("Идём дальше!")
                    reactions.go(INIT_STATE)
                } else {
                    reactions.sayRandom("А вот и нет! Это " + hint.author)
                    reactions.say("Идём дальше!")
                    reactions.go(INIT_STATE)
                }
            }
        }

        state("/game/go_on") {
            action {
//                val stage: Int = context.client["stage"] as Int
//                context.client["stage"] = stage + 1
                addStage(context)
                reactions.say("Идём дальше!")
                reactions.go("/game/ask")
            }
        }
    }
}

fun addStage(context: BotContext) {
    val stage: Int = context.client["stage"] as Int
    context.client["stage"] = stage + 1
}

