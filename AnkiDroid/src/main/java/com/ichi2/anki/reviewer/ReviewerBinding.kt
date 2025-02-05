/*
 * Copyright (c) 2025 Brayan Oliveira <brayandso.dev@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ichi2.anki.reviewer

import android.content.Context
import androidx.annotation.CheckResult
import com.ichi2.anki.R
import com.ichi2.anki.cardviewer.Gesture
import java.util.Objects

class ReviewerBinding(
    binding: Binding,
    val side: CardSide,
) : MappableBinding(binding) {
    override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) return false
        if (other !is ReviewerBinding) return false

        return side === CardSide.BOTH ||
            other.side === CardSide.BOTH ||
            side === other.side
    }

    override fun hashCode(): Int = Objects.hash(binding, PREFIX)

    override fun toPreferenceString(): String? {
        if (!binding.isValid) {
            return null
        }
        val s =
            StringBuilder()
                .append(PREFIX)
                .append(binding.toString())
        // don't serialise problematic bindings
        if (s.isEmpty()) {
            return null
        }
        when (side) {
            CardSide.QUESTION -> s.append(QUESTION_SUFFIX)
            CardSide.ANSWER -> s.append(ANSWER_SUFFIX)
            CardSide.BOTH -> s.append(QUESTION_AND_ANSWER_SUFFIX)
        }
        return s.toString()
    }

    override fun toDisplayString(context: Context): String {
        val formatString =
            when (side) {
                CardSide.QUESTION -> context.getString(R.string.display_binding_card_side_question)
                CardSide.ANSWER -> context.getString(R.string.display_binding_card_side_answer)
                CardSide.BOTH -> context.getString(R.string.display_binding_card_side_both) // intentionally no prefix
            }
        return String.format(formatString, binding.toDisplayString(context))
    }

    companion object {
        const val PREFIX = 'r'
        private const val QUESTION_SUFFIX = '0'
        private const val ANSWER_SUFFIX = '1'
        private const val QUESTION_AND_ANSWER_SUFFIX = '2'

        fun fromString(s: String): MappableBinding {
            val binding = s.substring(0, s.length - 1)
            val b = Binding.fromString(binding)
            val side =
                when (s[s.length - 1]) {
                    QUESTION_SUFFIX -> CardSide.QUESTION
                    ANSWER_SUFFIX -> CardSide.ANSWER
                    else -> CardSide.BOTH
                }
            return ReviewerBinding(b, side)
        }

        @CheckResult
        fun fromGesture(gesture: Gesture): ReviewerBinding = ReviewerBinding(Binding.GestureInput(gesture), CardSide.BOTH)
    }
}
