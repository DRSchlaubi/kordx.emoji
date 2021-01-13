package dev.kord.x.emoji

import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji

sealed class SkinTone(val unicode: String) {
    object Dark : SkinTone("\uD83C\uDFFF")
    object MediumDark : SkinTone("\uD83C\uDFFE")
    object Medium : SkinTone("\uD83C\uDFFD")
    object MediumLight : SkinTone("\uD83C\uDFFC")
    object Light : SkinTone("\uD83C\uDFFB")
    object Default : SkinTone("")


    companion object {
        val tones
            get() = listOf(  // compiler bug, don't remove the getter.
                Dark,
                MediumDark,
                Medium,
                MediumLight,
                Light,
                Default
            )
    }
}

/**
 * A Unicode emoji supported by Discord's client.
 */
sealed class DiscordEmoji {

    /**
     * The hex value of this emoji.
     */
    abstract val unicode: String

    /**
     * An emoji that supports [SkinTones][SkinTone].
     */
    data class Diverse(val code: String, val tone: SkinTone = SkinTone.Default) : DiscordEmoji() {
        fun withTone(tone: SkinTone): Diverse = copy(code = code, tone = tone)

        override val unicode: String
            get() = "$code${tone.unicode}"

        override fun toString(): String = unicode
    }

    /**
     * An generic emoji that does not support [SkinTones][SkinTone].
     */
    class Generic(override val unicode: String) : DiscordEmoji() {
        override fun toString(): String = unicode
    }
}

/**
 * Requests to add the [emoji] to the messages.
 */
suspend fun Message.addReaction(emoji: DiscordEmoji) = addReaction(emoji.toReaction())

/**
 * Requests to delete all [emoji] reactions from this message.
 */
suspend fun Message.deleteReaction(emoji: DiscordEmoji) = deleteReaction(emoji.toReaction())

/**
 * Requests to delete an emoji from this message made by this bot.
 */
suspend fun Message.deleteOwnReaction(emoji: DiscordEmoji) = deleteOwnReaction(emoji.toReaction())

/**
 * Transforms the emoji into a [ReactionEmoji.Unicode] emoji.
 */
fun DiscordEmoji.toReaction() = ReactionEmoji.Unicode(unicode)

fun ReactionEmoji.Companion.from(emoji: DiscordEmoji) = emoji.toReaction()

internal fun String.toSkinTone(): SkinTone? {

    return SkinTone.tones.firstOrNull { this.endsWith(it.unicode) }
}

internal fun String.removeTone(): String {
    return SkinTone.tones.fold(this) { acc, skinTone -> acc.removeSuffix(skinTone.unicode) }
}
