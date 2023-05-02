package com.rhnlf.storyapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rhnlf.storyapp.data.local.db.StoryDb
import com.rhnlf.storyapp.data.remote.api.ApiService
import com.rhnlf.storyapp.data.remote.response.ListStoryItem
import com.rhnlf.storyapp.data.remote.response.LoginResponse
import com.rhnlf.storyapp.data.remote.response.LoginResult
import com.rhnlf.storyapp.data.remote.response.RegisterResponse
import com.rhnlf.storyapp.data.remote.response.StoryResponse
import com.rhnlf.storyapp.data.remote.response.UploadStoryResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private var mockApi: ApiService = FakeApiService()
    private val mockDb: StoryDb = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(), StoryDb::class.java
    ).allowMainThreadQueries().build()
    private val token = "12345"

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb, mockApi, token
        )
        val pagingState = PagingState<Int, ListStoryItem>(
            listOf(), null, PagingConfig(10), 10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return RegisterResponse(false, "success")
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        return LoginResponse(LoginResult("saya", "1", "77777"), false, "success")
    }

    override suspend fun uploadStory(
        token: String, file: MultipartBody.Part, description: RequestBody, lat: Float?, lon: Float?
    ): UploadStoryResponse {
        return UploadStoryResponse(false, "success")
    }

    override suspend fun getAllStory(
        token: String, page: Int?, size: Int?, location: Int?
    ): StoryResponse {
        val item: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "https://assets.manutd.com/AssetPicker/images/0/0/16/247/1111858/Player_Profile_Thumbnail_Mens_2223_Kit_DDG1658217243574_thumb.jpg",
                "created at $i",
                "user $i",
                "description $i",
                i.toDouble(),
                "$i",
                i.toDouble()
            )
            item.add(story)
        }
        return StoryResponse(item, false, "success")
    }
}