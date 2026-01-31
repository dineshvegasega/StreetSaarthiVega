package com.ezatpanah.hilt_retrofit_paging_youtube.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ezatpanah.hilt_retrofit_paging_youtube.paging.MoviesPagingSource
import com.ezatpanah.hilt_retrofit_paging_youtube.repository.ApiRepository
import com.vegasega.streetsaarthi.models.ItemLiveScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(private val repository: ApiRepository) : ViewModel() {

    val loading = MutableLiveData<Boolean>()


//        val moviesList = Pager(PagingConfig(0)) {
//            MoviesPagingSource(repository, JSONObject())
//        }.flow.cachedIn(viewModelScope)

    fun getMovie(obj: JSONObject): Flow<PagingData<ItemLiveScheme>> {
        return Pager(PagingConfig(1)) {
            MoviesPagingSource(repository, obj)
        }.flow.cachedIn(viewModelScope)
    }


//    fun getMovie(obj: JSONObject): Flow<PagingData<ItemLiveScheme>> {
//        return Pager(PagingConfig(1)) {
//            MoviesPagingSource(repository, obj)
//        }.flow.cachedIn(viewModelScope)
//    }


//    val moviesList = Pager(PagingConfig(1)) {
//        MoviesPagingSource(repository, obj)
//    }.flow.cachedIn(viewModelScope)
//
//
//    //Api
//    val detailsMovie = MutableLiveData<MovieDetailsResponse>()
//    fun loadDetailsMovie(id: Int) = viewModelScope.launch {
//        loading.postValue(true)
//        val response = repository.getMovieDetails(id)
//        if (response.isSuccessful) {
//            detailsMovie.postValue(response.body())
//        }
//        loading.postValue(false)
//    }
}