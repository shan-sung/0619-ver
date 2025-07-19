package com.example.myapplication.navigation.routes

object Routes {

    // Root Layer
    object Root {
        const val LOGIN = "login"
        const val MAIN = "main"
    }

    // Main Tabs
    object App {
        const val EXPLORE = "explore"
        const val MY_PLANS = "my_plans"
        const val SAVED = "saved"
        const val PROFILE = "profile"
    }

    // Explore 頁與其子頁面
    object Explore {
        const val MAIN = "explore"

        object Featured {
            const val MAIN = "featured"
        }

        object Attraction {
            const val MAIN = "attraction"
        }

        object Recommend {
            const val MAIN = "recommendation"
        }
    }

    // MyPlans 區塊
    object MyPlans {
        const val MAIN = "my_plans"

        object Tab {
            const val CREATED = "trip_tab_created"
            const val PARTICIPATING = "trip_tab_participating"
        }

        const val CREATE = "create_trip"
        const val DETAIL = "trip_detail/{id}"
        const val CHAT = "trip_detail/{id}/chat"
        const val SELECT_FROM_SAVED = "select_from_saved"


        fun detailRoute(id: String) = "trip_detail/$id"
        fun chatRoute(id: String) = "trip_detail/$id/chat"
    }

    object Saved {
        const val MAIN = "saved"
    }

    object Profile {
        const val MAIN = "profile"
    }
}
