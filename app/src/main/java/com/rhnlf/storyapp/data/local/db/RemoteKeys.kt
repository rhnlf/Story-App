package com.rhnlf.storyapp.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val id: String, val prevKey: Int?, val nextKey: Int?
)