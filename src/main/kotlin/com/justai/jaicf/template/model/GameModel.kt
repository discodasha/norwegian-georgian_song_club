package com.justai.jaicf.template.model
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.justai.jaicf.context.BotContext
import java.io.File
import java.io.StringReader
import kotlin.random.Random

class Task(val text:String,
           val original: String,
           val author: String, val
           additional: ArrayList<String>)

fun loadTasks(): ArrayList<Task> {
    val jsonArray: String = Task::class.java.getResourceAsStream("/tasks.json")
        .bufferedReader()
        .use { it.readText() }

    val resArray = arrayListOf<Task>()
    val klaxon = Klaxon()
    JsonReader(StringReader(jsonArray)).use { reader ->
        reader.beginArray {
            while (reader.hasNext()) {
                val person = klaxon.parse<Task>(reader)
                if (person != null) {
                    resArray.add(person)
                }
            }
        }
    }
    return resArray
}


class GameModel() {
    var usedTasksIndexes: ArrayList<Int> = ArrayList()
    var tasksArray: ArrayList<Task> = loadTasks()

    fun getTask(): Task? {

        if (usedTasksIndexes.size == tasksArray.size) {
            return null
        }

        val possible: ArrayList<Int> = ArrayList()
        for (i in 0 until tasksArray.size) {
            if (i !in usedTasksIndexes) {
                possible.add(i)
            }
        }
        if (possible.size == 0) {
            return null
        }
        val index = Random.nextInt(0, possible.size)
        val taskIndex = possible[index]
        usedTasksIndexes.add(taskIndex)

        return tasksArray[taskIndex]
    }



}


fun getQuestion(task: Task?): String {
    if (task != null)
        return "\n\n" +task.text + "\n\n"
    return ""
}

class Hint(task: Task) {
    var answer : String = task.author
    var candidates: ArrayList<String> = task.additional
    var rightIndex: Int = -1
    var author: String = task.author
    var original: String = task.original

    init {
        answer.let { candidates.add(it) }
        candidates.shuffle()
        rightIndex = candidates.indexOf(answer)
    }
}

