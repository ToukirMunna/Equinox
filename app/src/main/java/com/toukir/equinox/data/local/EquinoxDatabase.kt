package com.toukir.equinox.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.toukir.equinox.data.local.converter.Converters
import com.toukir.equinox.data.local.dao.EmergencyTodoDao
import com.toukir.equinox.data.local.dao.LogDao
import com.toukir.equinox.data.local.dao.QuoteDao
import com.toukir.equinox.data.local.entity.EmergencyTodoEntity
import com.toukir.equinox.data.local.entity.LogEntryEntity
import com.toukir.equinox.data.local.entity.QuoteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [LogEntryEntity::class, EmergencyTodoEntity::class, QuoteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EquinoxDatabase : RoomDatabase() {

    abstract fun logDao(): LogDao
    abstract fun emergencyTodoDao(): EmergencyTodoDao
    abstract fun quoteDao(): QuoteDao

    companion object {
        @Volatile
        private var INSTANCE: EquinoxDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): EquinoxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EquinoxDatabase::class.java,
                    "equinox_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.emergencyTodoDao(), database.quoteDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(todoDao: EmergencyTodoDao, quoteDao: QuoteDao) {
            if (todoDao.getCount() == 0) {
                val defaultTodos = listOf(
                    EmergencyTodoEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Drink a large glass of ice-cold water",
                        targetProfile = "ALL",
                        orderIndex = 1
                    ),
                    EmergencyTodoEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Leave the room or get out of bed immediately",
                        targetProfile = "ALL",
                        orderIndex = 2
                    ),
                    EmergencyTodoEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Wash face with cold water",
                        targetProfile = "ALL",
                        orderIndex = 3
                    ),
                    EmergencyTodoEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Do 20 push-ups or 30 jumping jacks",
                        targetProfile = "UNMARRIED",
                        orderIndex = 4
                    ),
                    EmergencyTodoEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Place phone in another room or turn off screen",
                        targetProfile = "ALL",
                        orderIndex = 5
                    ),
                    EmergencyTodoEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Move away from private room & sit with spouse",
                        targetProfile = "MARRIED",
                        orderIndex = 4
                    )
                )
                todoDao.insertTodos(defaultTodos)
            }

            if (quoteDao.getCount() == 0) {
                val defaultQuotes = listOf(
                    QuoteEntity(
                        id = UUID.randomUUID().toString(),
                        quote = "An urge is only a temporary brain wave. You don't have to obey it — just ride it until it fades.",
                        author = "Equinox Mindset",
                        targetProfile = "ALL"
                    ),
                    QuoteEntity(
                        id = UUID.randomUUID().toString(),
                        quote = "Discipline is choosing between what you want now and what you want most.",
                        author = "Abraham Lincoln",
                        targetProfile = "UNMARRIED"
                    ),
                    QuoteEntity(
                        id = UUID.randomUUID().toString(),
                        quote = "Real intimacy is built in reality with your partner, not on a glowing screen.",
                        author = "Equinox Principle",
                        targetProfile = "MARRIED"
                    ),
                    QuoteEntity(
                        id = UUID.randomUUID().toString(),
                        quote = "Every urge you conquer rewires your neural pathways toward true freedom.",
                        author = "Neuroscience Insight",
                        targetProfile = "ALL"
                    )
                )
                quoteDao.insertQuotes(defaultQuotes)
            }
        }
    }
}
