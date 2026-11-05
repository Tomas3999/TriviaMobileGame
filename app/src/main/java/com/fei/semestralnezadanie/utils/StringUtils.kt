package com.fei.semestralnezadanie.utils

import androidx.core.text.HtmlCompat

/**
 * Dekóduje HTML entity v texte (napr. &quot; -> ", &#039; -> ', &amp; -> &)
 */
fun String.decodeHtml(): String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
}
