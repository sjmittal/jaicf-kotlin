package com.justai.jaicf.channel.jaicp.channels

import com.justai.jaicf.BotEngine
import com.justai.jaicf.api.BotApi
import com.justai.jaicf.channel.jaicp.JaicpLiveChatProvider
import com.justai.jaicf.channel.jaicp.bargein.BargeInProcessor
import com.justai.jaicf.channel.jaicp.dto.JaicpBotRequest
import com.justai.jaicf.channel.jaicp.dto.TelephonyBotRequest
import com.justai.jaicf.channel.jaicp.dto.bargein.BargeInProperties
import com.justai.jaicf.channel.jaicp.reactions.TelephonyReactions

/**
 * JAICP Telephony channel
 *
 * This channel can be used for processing incoming and outgoing calls.
 * TelephonyChannel can be configured only in JAICP Web Interface.
 * Caller information can be retrieved from [TelephonyBotRequest].
 * Telephony channel can receive channel-specific [TelephonyEvents].
 *
 * See example telephony bot: https://github.com/just-ai/jaicf-kotlin/tree/master/examples/jaicp-telephony
 *
 * @see TelephonyReactions
 * @see TelephonyBotRequest
 * @see TelephonyEvents
 * @see JaicpNativeChannel
 *
 * @param botApi the [BotApi] implementation used to process the requests to this channel
 * */
class TelephonyChannel(
    override val botApi: BotApi,
    private val bargeInProcessor: BargeInProcessor = BargeInProcessor.NON_FALLBACK,
    private val defaultBargeInProperties: BargeInProperties = BargeInProperties.DEFAULT,
    mutedEvents: List<String> = emptyList()
) : JaicpNativeChannel(botApi, mutedEvents) {

    init {
        (botApi as? BotEngine)?.hooks?.apply {
            addHookAction(bargeInProcessor::handleBeforeProcess)
            addHookAction(bargeInProcessor::handleBeforeActivation)
            addHookAction(bargeInProcessor::handleActivationError)
        }
    }

    override fun createRequest(request: JaicpBotRequest) = TelephonyBotRequest.create(request)

    override fun createReactions() = TelephonyReactions(defaultBargeInProperties)

    companion object : JaicpNativeChannelFactory {
        override val channelType = "resterisk"
        override fun create(botApi: BotApi, liveChatProvider: JaicpLiveChatProvider): JaicpNativeBotChannel =
            TelephonyChannel(botApi)
    }

    class Factory(
        private val bargeInProcessor: BargeInProcessor = BargeInProcessor.NON_FALLBACK,
        private val defaultBargeInProperties: BargeInProperties = BargeInProperties.DEFAULT,
        private val mutedEvents: List<String> = emptyList()
    ) : JaicpNativeChannelFactory {
        override val channelType = "resterisk"
        override fun create(botApi: BotApi, liveChatProvider: JaicpLiveChatProvider): JaicpNativeBotChannel =
            TelephonyChannel(botApi, bargeInProcessor, defaultBargeInProperties, mutedEvents)
    }
}
