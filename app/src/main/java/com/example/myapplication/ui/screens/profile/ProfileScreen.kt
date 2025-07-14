package com.example.myapplication.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.User

@Composable
fun ProfileScreen(user: User) {
    val age = remember { user.getAge() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileItem(label = "使用者名稱", value = user.username)
        ProfileItem(label = "電子郵件", value = user.email)
        ProfileItem(label = "MBTI", value = user.mbti)
        ProfileItem(label = "生日", value = user.birthday.toString())
        ProfileItem(label = "年齡", value = "$age 歲")
        ProfileItem(label = "電話", value = user.phoneNumber)
        ProfileItem(label = "個人簡介", value = user.bio ?: "未填寫")
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp)
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
}