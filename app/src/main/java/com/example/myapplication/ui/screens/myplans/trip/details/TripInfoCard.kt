package com.example.myapplication.ui.screens.myplans.trip.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.CurrentUser
import com.example.myapplication.model.Travel
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.components.InfoCard
import com.example.myapplication.ui.components.RoundedIconButton
import com.example.myapplication.ui.components.toInfoCardData

@Composable
fun TripInfoCard(
    navController: NavController,
    travel: Travel,
    showButton: Boolean
) {
    val cardData = travel.toInfoCardData(navController, showButton = showButton)

    Box {
        InfoCard(
            data = cardData.copy(
                buttonText = null,
                onButtonClick = null
            ),
            width = 360.dp,
            height = 200.dp
        )

        if (showButton) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val currentUserId = CurrentUser.user?.id
                val isInTrip = travel.userId == currentUserId || travel.members.contains(currentUserId)

                if (isInTrip) {
                    RoundedIconButton(
                        icon = Icons.Default.MonetizationOn,
                        description = "預算",
                        onClick = {
                            navController.navigate(Routes.MyPlans.chatRoute(travel._id.orEmpty()))
                        }
                    )
                    RoundedIconButton(
                        icon = Icons.Default.Chat,
                        description = "聊天室",
                        onClick = {
                            navController.navigate(Routes.MyPlans.chatRoute(travel._id.orEmpty()))
                        }
                    )
                    RoundedIconButton(
                        icon = Icons.Default.PersonAdd,
                        description = "分享行程",
                        onClick = {
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("show_share_dialog", travel._id)
                        }
                    )
                }
            }
        }
    }
}