package fi.paytrail.demo

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.paytrail.demo.repository.ShoppingCartRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// TODO: support SavedStateHandle to persist state across activity recreation

@HiltViewModel
class ShoppingCartViewModel @Inject constructor(
    repository: ShoppingCartRepository,
) : ViewModel() {
    val items = repository.cart().map { it.items.values.toList() }
    val totalAmount = repository.cart().map { it.totalAmount }
    val rowCount = repository.cart().map { it.items.size }
}
