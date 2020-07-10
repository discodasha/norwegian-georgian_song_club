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
    val ASK_STATE = "/game/ask"

    init {
        state("/game") {
            action {
                context.client["score"] = 0
                reactions.run{
                    sayRandom("Начинаем играть!\n", "Погнали!\n", "Стартуем!\n")
                    go(INIT_STATE)
                }
//                reactions.sayRandom("Начинаем играть!\n", "Погнали!\n", "Стартуем!\n")
//                reactions.go(INIT_STATE)
            }
        }

        state("/game/init") {
            action {
                val task: Task? = game.getTask()
                if (task == null) {
                    reactions.go("end")
                } else {
                    val hint = Hint(task)
                    context.client["stage"] = 0
                    context.client["hint"] = hint
                    reactions.say(getQuestion(task))
                    reactions.go(ASK_STATE)
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
                    reactions.run {
                        sayRandom("А вот и нет! Это всё-таки" + hint.author + "!\n")
                        go(INIT_STATE)
                    }
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
                    addScore(context)
                    reactions.run {
                        say("Правильно! Если слушать оригинал, то там поётся \"" + hint.original + "\"\n")
                        say("Идём дальше!")
                        go(INIT_STATE)
                    }
//                    reactions.say("Правильно! Если слушать оригинал, то там поётся \"" + hint.original + "\"")
//                    reactions.say("Идём дальше!")
//                    reactions.go(INIT_STATE)
                } else {
                    reactions.run {
                        sayRandom("А вот и нет! Это " + hint.author)
                        say("Идём дальше!")
                        go(INIT_STATE)
                    }
//                    reactions.sayRandom("А вот и нет! Это " + hint.author)
//                    reactions.say("Идём дальше!")
//                    reactions.go(INIT_STATE)
                }
            }
        }

        state("/game/go_on") {
            action {
                addStage(context)
                reactions.run {
                    say("Идём дальше!")
                    go(ASK_STATE)
                }
//                reactions.say("Идём дальше!")
//                reactions.go(ASK_STATE)
            }
        }

//        fallback {
//            reactions.say("Я понимаю, что я нудная, но скажи ДА или НЕТ.")
//            reactions.go("/game/ask")
//        }

        state("garbage") {
            activators {
                catchAll()
            }

            action {
                reactions.say("Я понимаю, что я нудная, но скажи ДА или НЕТ.")
                reactions.go("/game/ask")
            }
        }
    }
}

fun addStage(context: BotContext) {
    val stage: Int = context.client["stage"] as Int
    context.client["stage"] = stage + 1
}

fun addScore(context: BotContext) {
    var score = context.client["score"] as Int
    score ++
    context.client["score"] = score
}