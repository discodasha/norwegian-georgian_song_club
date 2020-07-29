package com.justai.jaicf.template.scenario

import com.justai.jaicf.activator.caila.caila
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

    val game = GameModel()
    val INIT_STATE = "/game/init"
    val GO_ON_STATE = "/game/go_on"
    val ASK_STATE = "/game/ask"
    val goOnString: String = "Идём дальше!\n"

    init {
        state("/game") {
            action {
                context.client["score"] = 0
                reactions.run {
                    sayRandom("Начинаем играть!\n", "Погнали!\n", "Стартуем!\n")
                    go(INIT_STATE)
                }
            }


            state("/game/init") {
                action {
                    val task: Task? = game.getTask()
                    if (task == null) {
                        reactions.say("Кажется, мы всё с вами разыграли! Собрание закончилось, и песни тоже.")
                        reactions.go("/end")
                    } else {
                        val hint = Hint(task)
                        context.client["stage"] = 0
                        context.client["hint"] = hint
                        if (game.usedTasksIndexes.size > 1)
                            reactions.sayRandom("Продолжим!", "Погнали дальше!", "Итак, новый вопрос!")
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
                    reactions.buttons("Да", "Нет")
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
                            sayRandom("А вот и нет! Это всё-таки " + hint.author + "!\n")
                            say(goOnString)
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
                        reactions.say("Правильно! Если слушать оригинал, то там поётся:\n\"" + hint.original + "\"\n\n")
                    } else {
                        reactions.sayRandom("А вот и нет! Это " + hint.author + "!\n")
                    }
                    reactions.run {
                        //say(goOnString)
                        go(INIT_STATE)
                    }
                }
            }

            state("/game/go_on") {
                action {
                    addStage(context)
                    reactions.run {
                        sayRandom("Не то!", "Мимо, не то!", "Не а")
                        if (!game.isEnd())
                            say(goOnString)
                        go(ASK_STATE)
                    }
                }
            }

            state("/game/guess_singer"){
                activators {
                    intent("guess_singer")
                }

                action {
                    val hint = context.client["hint"] as Hint
                    val entity = activator.caila?.entities?.get(0)?.value
                    if (hint.answer == entity) {
                        addScore(context)
                        reactions.say("Правильно! Если слушать оригинал, то там поётся:\n\"" + hint.original + "\"\n\n")
                        reactions.go(INIT_STATE)
                    }
                    else {
                        reactions.go(GO_ON_STATE)
                    }
                }
            }

            fallback("f1") {
                reactions.run {
                    say("Эээ, непонятно. Переспрошу.")
                    buttons("Да", "Нет")
                    reactions.go("/game/ask")
                }
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