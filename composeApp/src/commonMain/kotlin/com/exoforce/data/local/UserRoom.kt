package com.exoforce.data.local

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.exoforce.data.domain.User
import com.exoforce.data.mapper.toDomain
import com.exoforce.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String?,
    val name: String?,
    @ColumnInfo(name = "phone_number") val phoneNumber: String?,
    @ColumnInfo(name = "weight_kg") val weightKg: Float?,
    @ColumnInfo(name = "height_cm") val heightCm: Float?,
    @ColumnInfo(name = "access_token") val accessToken: String,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
    @ColumnInfo(name = "email_verified_at") val emailVerifiedAt: Instant?,
    @ColumnInfo(name = "phone_number_verified_at") val phoneNumberVerifiedAt: Instant?,
    val admin: Boolean,
)

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE access_token != '' LIMIT 1")
    fun observeMe(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE access_token != '' LIMIT 1")
    suspend fun me(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun clear()
}


class UserLocalDataSource(
    private val userDao: UserDao
)  {

    fun observeMe(): Flow<User?> =
        userDao.observeMe()
            .map { entity -> entity?.toDomain() }
            .distinctUntilChanged()

    suspend fun me(): User? = userDao.me()?.toDomain()

    suspend fun upsert(user: User) {
        userDao.upsert(user.toEntity())
    }

    suspend fun clear() {
        userDao.clear()
    }
}


