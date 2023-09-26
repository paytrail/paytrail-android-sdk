package fi.paytrail.demo.shoppingcart

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

// TODO: support SavedStateHandle to persist state across activity recreation
@HiltViewModel
class ShoppingCartViewModel @Inject constructor(
    private val repository: ShoppingCartRepository,
) : ViewModel() {
    val items = repository.cart().map { it.items.values.toList() }
    val totalAmount = repository.cart().map { it.totalAmount }
    val rowCount = repository.cart().map { it.items.size }

    fun incrementAmount(id: UUID) {
        repository.incrementItemAmount(id)
    }
    fun decrementAmount(id: UUID){
        repository.decrementItemAmount(id)
    }
}
