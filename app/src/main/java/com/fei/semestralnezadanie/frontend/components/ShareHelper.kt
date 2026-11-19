package com.fei.semestralnezadanie.frontend.components

import android.content.Context
import android.content.Intent
import com.fei.semestralnezadanie.R

object ShareHelper {

    /**
     * Otvorí natívne systémové okno pre zdieľanie výsledku (Bod F zadania).
     */
    fun shareResult(
        context: Context,
        score: Int,
        totalQuestions: Int,
        categoryName: String,
        durationSeconds: Long
    ) {
        // Načítame predformátovaný text zo strings.xml s dosadenými hodnotami
        val shareMessage = context.getString(
            R.string.result_share_text,
            score,
            totalQuestions,
            categoryName,
            durationSeconds
        )

        // Vytvorenie natívneho Intent.ACTION_SEND
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }

        // Zobrazenie ponuky aplikácií na zdieľanie
        val chooserIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(chooserIntent)
    }
}