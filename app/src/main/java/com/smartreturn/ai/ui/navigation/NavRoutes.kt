package com.smartreturn.ai.ui.navigation

/**
 * Centralised navigation route definitions.
 * All screen destinations are declared here to avoid string literals scattered
 * throughout the codebase.
 */
object NavRoutes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val ADD_ITEM = "add_item"
    const val SCAN = "scan"
    const val DETECTION_RESULT = "detection_result"
    const val MEMORY = "memory"
    const val MEMORY_REPLAY = "memory_replay/{itemId}"
    const val ASK_SMARTRETURN = "ask_smartreturn"
    const val ALERT = "alert"
    const val SETTINGS = "settings"

    /** Build the memory replay route with a specific item id. */
    fun memoryReplay(itemId: Long): String = "memory_replay/$itemId"
}
