package com.fei.semestralnezadanie

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fei.semestralnezadanie.ui.theme.amber
import com.fei.semestralnezadanie.ui.theme.blue
import com.fei.semestralnezadanie.ui.theme.green
import com.fei.semestralnezadanie.ui.theme.orange
import com.fei.semestralnezadanie.ui.theme.purple
import com.fei.semestralnezadanie.ui.theme.red

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
        val AVATAR_BORDER_WIDTH = 2.dp
        val AVATAR_INNER_ICON_SIZE = 64.dp
        val PROGRESS_INDICATOR_SIZE = 24.dp

        val DIFFICULTY_EASY = green
        val DIFFICULTY_MEDIUM = orange
        val DIFFICULTY_HARD = red
        val DIFFICULTY_ANY = blue

        val TYPE_ANY = blue
        val TYPE_MULTIPLE = purple
        val TYPE_TRUE_FALSE = green
    }

    object Api {
        const val BASE_URL = "https://opentdb.com/"
        const val CATEGORIES_ENDPOINT = "api_category.php"
        const val QUESTIONS_ENDPOINT = "api.php"
        const val TOKEN_ENDPOINT = "api_token.php"
        const val COUNT_ENDPOINT = "api_count.php"
    }

    object Trivia {
        const val QUESTION_TIME_LIMIT_SECONDS = 15L
        const val CLASSIC_TRIVIA_QUESTION_COUNT = 10
        const val MARATHON_BATCH_SIZE = 50
        const val QUESTION_TIME_LIMIT_WARNING = 5L
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
        const val KEY_LANGUAGE_CODE = "key_language_code"
    }

    object Notifications {
        const val CHANNEL_ID = "trivia_reminders_channel"
        const val CHANNEL_NAME = "Trivia Reminders"
        const val STREAK_REMINDER_WORK_TAG = "streak_reminder_work"
        const val INACTIVITY_REMINDER_WORK_TAG = "inactivity_reminder_work"

        // ID jednotlivých notifikácií
        const val NOTIFICATION_ID_STREAK = 1001
        const val NOTIFICATION_ID_INACTIVITY = 1002
        const val NOTIFICATION_ID_MARATHON = 1003

        // Pravidlá časovania pre WorkManager
        const val INACTIVITY_THRESHOLD_DAYS = 3L
        const val REMINDER_INTERVAL_HOURS = 12L

        const val NOTIFICATIONS_DEFAULT_ENABLED = false
    }

    object App {
        const val LOADER_DELAY_MILLIS = 400L
        const val DEFAULT_LANGUAGE_CODE = "sk"
        val SUPPORTED_LANGUAGES = listOf("sk", "en")
    }
}