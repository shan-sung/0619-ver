package com.example.myapplication.navigation.routes

object Routes {

    object Root {
        const val LOGIN = "login"
        const val MAIN = "main"
    }

    // Tab 名稱（僅作為選擇狀態識別，不參與 navigation）
    object AppTabs {
        const val EXPLORE = "explore"
        const val MY_PLANS = "my_plans"
        const val SAVED = "saved"
        const val PROFILE = "profile"
        const val FRIEND = "friend"
    }

    // Explore 模組
    object Explore {
        const val MAIN = "explore"
        const val FEATURED = "explore/featured"
        const val ATTRACTION = "explore/attraction"
        const val RECOMMENDATION = "explore/recommendation"
    }

    // MyPlans 模組
    object MyPlans {
        const val MAIN = "my_plans"
        const val CREATE = "my_plans/create"
        const val DETAIL = "my_plans/detail/{id}"
        const val CHAT = "my_plans/detail/{id}/chat"
        const val SELECT_FROM_SAVED = "my_plans/select_from_saved"
        const val PREVIEW = "my_plans/preview"
        const val SEARCH = "my_plans/search"
        const val ADD_SCHEDULE = "my_plans/add_schedule"

        fun detailRoute(id: String) = "my_plans/detail/$id"
        fun chatRoute(id: String) = "my_plans/detail/$id/chat"

        object Tab {
            const val CREATED = "created"
            const val PARTICIPATING = "participating"
        }
    }

    // Saved 模組
    object Saved {
        const val MAIN = "saved"
    }

    // Friend 模組
    object Friend {
        const val MAIN = "friend"
    }

    // Profile 模組
    object Profile {
        const val MAIN = "profile"
    }
}