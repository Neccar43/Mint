package com.novacodestudios.mint.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.novacodestudios.mint.data.repository.MintRepository
import com.novacodestudios.mint.presentation.addition.AdditionViewModel
import com.novacodestudios.mint.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
private val repository: MintRepository,
) : ViewModel() {
    var state by mutableStateOf(SettingsState())
        private set

    private val _eventFlow= MutableSharedFlow<String>()
    val eventFlow=_eventFlow.asSharedFlow()

    fun onEvent(event: SettingsEvent){
        when (event) {
            SettingsEvent.OnEraseData -> deleteAllData()
        }
    }

    private fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllData().collectLatest {resource->
                state = when (resource) {
                    is Resource.Error -> {
                        _eventFlow.emit(resource.exception.message?:"error")
                        state.copy(isLoading = false, error = resource.exception.message)
                    }
                    Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> {
                        _eventFlow.emit("Data erase successfully")
                        state.copy(isLoading = false, error = null)
                    }
                }

            }
        }
    }
    /*sealed class UIEvent{
        data class ShowSnackbar(val message: String) :UIEvent()
        data object Submitted :UIEvent()
    }*/
}

sealed class SettingsEvent{
    data object OnEraseData:SettingsEvent()
}

data class SettingsState(
    val isLoading:Boolean=false,
    val error:String?=null,
)