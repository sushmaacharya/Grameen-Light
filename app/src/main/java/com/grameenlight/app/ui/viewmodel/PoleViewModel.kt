package com.grameenlight.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grameenlight.app.data.local.PoleEntity
import com.grameenlight.app.data.repository.PoleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PoleUiState(
    val poles: List<PoleEntity> = emptyList(),
    val selectedPole: PoleEntity? = null
)

class PoleViewModel(private val poleRepository: PoleRepository) : ViewModel() {
    private val selectedPoleId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PoleUiState> = combine(
        poleRepository.poles,
        selectedPoleId
    ) { poles, selectedId ->
        PoleUiState(
            poles = poles,
            selectedPole = poles.firstOrNull { it.poleId == selectedId }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PoleUiState())

    init {
        viewModelScope.launch { poleRepository.ensureSeeded() }
    }

    fun selectPole(poleId: String) {
        selectedPoleId.value = poleId
    }
}
