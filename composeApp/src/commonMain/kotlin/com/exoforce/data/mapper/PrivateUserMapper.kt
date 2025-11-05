package com.exoforce.data.mapper

import com.exoforce.data.domain.PrivateUser
import com.exoforce.data.remote.types.RemotePrivateUser

fun RemotePrivateUser.toDomain(): PrivateUser {
    return PrivateUser(
        id = this.id,
        email = this.email,
        name = this.name,
        phoneNumber = this.phoneNumber,
        weightKg = this.weightKg,
        heightCm = this.heightCm,
        accessToken = this.accessToken,
        createdAt = this.createdAt,
    )
}