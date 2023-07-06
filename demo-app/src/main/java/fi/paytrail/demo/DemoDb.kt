package fi.paytrail.demo

import androidx.room.Database
import androidx.room.RoomDatabase
import fi.paytrail.demo.tokenization.TokenizedCard
import fi.paytrail.demo.tokenization.TokenizedCardDao

@Database(entities = [TokenizedCard::class], version = 1)
abstract class DemoDb : RoomDatabase() {
    abstract fun tokenizedCardDao(): TokenizedCardDao
}
