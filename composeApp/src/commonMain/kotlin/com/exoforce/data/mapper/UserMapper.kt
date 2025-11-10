package com.exoforce.data.mapper

import com.exoforce.data.domain.User
import com.exoforce.data.local.UserEntity
import com.exoforce.data.remote.types.RemoteUser

fun RemoteUser.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        name = this.name,
        phoneNumber = this.phoneNumber,
        weightKg = this.weightKg,
        heightCm = this.heightCm,
        accessToken = this.accessToken,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        emailVerifiedAt = this.emailVerifiedAt,
        phoneNumberVerifiedAt = this.phoneNumberVerifiedAt,
        admin = this.admin,
    )
}

fun UserEntity.toDomain(): User =
    User(
        id = id,
        email = email,
        name = name,
        phoneNumber = phoneNumber,
        weightKg = weightKg,
        heightCm = heightCm,
        accessToken = accessToken,
        createdAt = createdAt,
        updatedAt = updatedAt,
        emailVerifiedAt = emailVerifiedAt,
        phoneNumberVerifiedAt = phoneNumberVerifiedAt,
        admin = admin,
    )

fun User.toEntity(): UserEntity =
    UserEntity(
        id = id,
        email = email,
        name = name,
        phoneNumber = phoneNumber,
        weightKg = weightKg,
        heightCm = heightCm,
        accessToken = accessToken,
        createdAt = createdAt,
        updatedAt = updatedAt,
        emailVerifiedAt = emailVerifiedAt,
        phoneNumberVerifiedAt = phoneNumberVerifiedAt,
        admin = admin,
    )
