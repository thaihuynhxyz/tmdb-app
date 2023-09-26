package xyz.thaihuynh.tmdb.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.thaihuynh.tmdb.data.Movie
import xyz.thaihuynh.tmdb.data.MovieRepository
import xyz.thaihuynh.tmdb.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val id =
        savedStateHandle.get<String>("id")?.toInt() ?: throw IllegalArgumentException("Missing id")

    private val _stateFlow = MutableStateFlow<Resource<Movie>>(Resource.Loading)
    val stateFlow: StateFlow<Resource<Movie>>
        get() = _stateFlow

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.getMovie(id).collect {
                _stateFlow.tryEmit(it)
            }
        }
    }
}