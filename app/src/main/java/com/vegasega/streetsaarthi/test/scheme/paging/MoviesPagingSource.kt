package com.ezatpanah.hilt_retrofit_paging_youtube.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ezatpanah.hilt_retrofit_paging_youtube.repository.ApiRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vegasega.streetsaarthi.models.BaseResponseDC
import com.vegasega.streetsaarthi.models.ItemLiveScheme
import org.json.JSONObject
import retrofit2.HttpException

class MoviesPagingSource(
    private val repository: ApiRepository,
    obj: JSONObject,
) : PagingSource<Int, ItemLiveScheme>() {
    val jSONObject = obj
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemLiveScheme> {
        return try {
            val currentPage = params.key ?: 1
            val response = repository.getPopularMoviesList(jSONObject.apply {
                put("page", currentPage)
            })
            val data = response.body() as BaseResponseDC<Any>
            val typeToken = object : TypeToken<List<ItemLiveScheme>>() {}.type
            val changeValue =
                Gson().fromJson<List<ItemLiveScheme>>(Gson().toJson(data.data), typeToken)
//            Log.e("TAG", "changeValue "+changeValue.toString())
            val responseData = mutableListOf<ItemLiveScheme>()

            responseData.addAll(changeValue)

            LoadResult.Page(
                data = responseData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = currentPage.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }

    }


    override fun getRefreshKey(state: PagingState<Int, ItemLiveScheme>): Int? {
        return null
    }


}