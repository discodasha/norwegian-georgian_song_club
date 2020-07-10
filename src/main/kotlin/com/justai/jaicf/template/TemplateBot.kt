package com.justai.jaicf.template

import com.justai.jaicf.BotEngine
import com.justai.jaicf.activator.regex.RegexActivator
import com.justai.jaicf.activator.catchall.CatchAllActivator
import com.justai.jaicf.activator.event.BaseEventActivator
import com.justai.jaicf.channel.googleactions.dialogflow.ActionsDialogflowActivator
import com.justai.jaicf.channel.yandexalice.activator.AliceIntentActivator
import com.justai.jaicf.context.manager.InMemoryBotContextManager
import com.justai.jaicf.context.manager.BotContextManager
import com.justai.jaicf.template.scenario.MainScenarioTest
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI


//private val contextManager = System.getenv("MONGODB_URI")?.let { url ->
//    val uri = MongoClientURI(url)
//    val client = MongoClient(uri)
//    MongoBotContextManager(client.getDatabase(uri.database!!).getCollection("contexts"))
//
//} ?: InMemoryBotContextManager



val templateBot = BotEngine(
    model = MainScenarioTest.model,
    activators = arrayOf(
        AliceIntentActivator,
        RegexActivator,
        CatchAllActivator,
        BaseEventActivator
    )
)