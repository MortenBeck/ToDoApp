package dk.dtu.ToDoList.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeScreenViewModel : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog

    fun updateSearchText(text: String) {
        _searchText.value = text
    }

    fun toggleAddDialog(visible: Boolean) {
        _showAddDialog.value = visible
    }
}
