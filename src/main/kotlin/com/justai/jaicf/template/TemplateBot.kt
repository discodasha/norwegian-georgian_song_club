package com.justai.jaicf.template

import com.justai.jaicf.BotEngine
import com.justai.jaicf.activator.caila.CailaIntentActivator
import com.justai.jaicf.activator.event.BaseEventActivator
import com.justai.jaicf.channel.yandexalice.activator.AliceIntentActivator
import com.justai.jaicf.template.scenario.MainScenarioTest
import java.util.*

import com.justai.jaicf.activator.caila.CailaNLUSettings
import com.justai.jaicf.activator.catchall.CatchAllActivator
import com.justai.jaicf.activator.regex.RegexActivator

val accessToken: String = System.getenv("JAICP_API_TOKEN") ?: Properties().run {
    load(CailaNLUSettings::class.java.getResourceAsStream("/jaicp.properties"))
    getProperty("apiToken")
}

private val cailaNLUSettings = CailaNLUSettings(
    accessToken = accessToken,
    confidenceThreshold = 0.2,
    classifierNBest = 5,
    cailaUrl = "https://jaicf01-demo-htz.lab.just-ai.com/cailapub/api/caila/p"
)


val templateBot = BotEngine(
    model = MainScenarioTest.model,
    activators = arrayOf(
        AliceIntentActivator,
        CailaIntentActivator.Factory(cailaNLUSettings),
        RegexActivator,
        CatchAllActivator,
        BaseEventActivator
    )
)