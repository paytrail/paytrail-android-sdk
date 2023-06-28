package fi.paytrail.demo.tokenization

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Database(entities = [TokenizedCard::class], version = 1)
abstract class TokenizedCardDb : RoomDatabase() {
    abstract fun tokenizedCardDao(): TokenizedCardDao
}

@Entity("tokenized_card")
data class TokenizedCard(
    @PrimaryKey val tokenizationId: String,
)

@Dao
interface TokenizedCardDao {
    @Query("SELECT * FROM tokenized_card")
    fun getAll(): Flow<List<TokenizedCard>>

    @Insert
    suspend fun insertAll(vararg cards: TokenizedCard)

    @Delete
    suspend fun delete(card: TokenizedCard)
}
