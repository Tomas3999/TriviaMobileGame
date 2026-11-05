package com.fei.semestralnezadanie

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Config {
    object UI {
        val FONT_SIZE_CAPTION = 12.sp
        val FONT_SIZE_BODY_SMALL = 14.sp
        val FONT_SIZE_BODY_MEDIUM = 16.sp
        val FONT_SIZE_BODY_LARGE = 18.sp
        val FONT_SIZE_TITLE = 20.sp
        val FONT_SIZE_QUESTION = 22.sp
        val FONT_SIZE_HEADER = 26.sp
        val FONT_SIZE_TIMER = 32.sp

        val SPACING_EXTRA_SMALL = 4.dp
        val SPACING_SMALL = 8.dp
        val SPACING_MEDIUM = 16.dp
        val SPACING_LARGE = 24.dp
        val SPACING_EXTRA_LARGE = 32.dp

        val CORNER_RADIUS_SMALL = 8.dp
        val CORNER_RADIUS_MEDIUM = 16.dp
        val CORNER_RADIUS_LARGE = 24.dp

        val BUTTON_HEIGHT = 56.dp
        val AVATAR_SIZE_SMALL = 40.dp
        val AVATAR_SIZE_LARGE = 100.dp
    }

    object Api {
        const val BASE_URL = "https://opentdb.com/"
        const val CATEGORIES_ENDPOINT = "api_category.php"
        const val QUESTIONS_ENDPOINT = "api.php"
    }

    object Trivia {
        const val QUESTION_TIME_LIMIT_SECONDS = 15L
        const val CLASSIC_TRIVIA_QUESTION_COUNT = 10
        const val MARATHON_BATCH_SIZE = 50
    }

    object Gamification {
        const val STREAK_TARGET_DAYS = 7
        const val STREAK_MIN_ACCURACY_PERCENT = 80.0
    }

    object DataStore {
        const val PREFERENCES_NAME = "user_preferences"
        const val KEY_USER_NAME = "key_user_name"
        const val KEY_AVATAR_URI = "key_avatar_uri"
        const val KEY_IS_ONBOARDING_COMPLETED = "key_is_onboarding_completed"
    }

    object Notifications {
        const val CHANNEL_ID = "trivia_reminders_channel"
        const val CHANNEL_NAME = "Trivia Reminders"
        const val STREAK_REMINDER_WORK_TAG = "streak_reminder_work"
        const val INACTIVITY_REMINDER_WORK_TAG = "inactivity_reminder_work"
    }
}