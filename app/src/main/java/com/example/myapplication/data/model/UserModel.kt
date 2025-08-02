package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.Period

data class User(
    val id: String,
    val username: String,
    val email: String,
    val mbti: String,
    val birthday: LocalDate? = null,
    val phoneNumber: String,
    val bio: String? = null,
    val avatarUrl: String? = null,
    val friends: List<String> = emptyList()
) {
    fun getAge(): Int = Period.between(birthday, LocalDate.now()).years

    fun toSummary(
        tripCount: Int? = null,
        followerCount: Int? = null
    ) = UserSummary(
        id = id,
        username = username,
        mbti = mbti,
        avatarUrl = avatarUrl,
        email = email,
        birthday = birthday?.toString(),
        bio = bio,
        tripCount = tripCount,
        followerCount = followerCount
    )
}

data class UserSummary(
    @SerializedName("_id") val id: String,
    val username: String,
    val mbti: String,
    val avatarUrl: String? = null,
    val email: String? = null,
    val birthday: String? = null,
    val tripCount: Int? = null,
    val followerCount: Int? = null,
    val bio: String? = null
)

data class FriendRequestBody(
    @SerializedName("from_user_id") val fromUserId: String,
    @SerializedName("to_user_id") val toUserId: String
)


data class FriendResponseBody(
    val from_user_id: String,
    val accept: Boolean
)

data class FriendRequest(
    val fromUserId: String,
    val toUserId: String,
    val status: String,
    val timestamp: String,
    val fromUsername: String,
    val fromAvatarUrl: String
)

data class AddMembersRequest(
    val memberIds: List<String>
)

data class LoginRequest(
    val email: String,
    val password: String
)
data class LoginResponse(
    val user: User,
    val token: String,
    val friends: List<UserSummary>
)
