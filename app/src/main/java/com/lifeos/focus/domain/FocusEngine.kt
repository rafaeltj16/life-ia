package com.lifeos.focus.domain

import com.lifeos.focus.domain.model.FocusSession

class FocusEngine {

    fun calculateScore(
        session: FocusSession
    ): Int {
        var score = 100
        score -= session.interruptions * 10
        score -= session.pauses * 5
        return score.coerceAtLeast(0)
    }
}
