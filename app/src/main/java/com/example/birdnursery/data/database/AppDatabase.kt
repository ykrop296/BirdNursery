package com.example.birdnursery.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.data.model.Cage
import com.example.birdnursery.data.model.FeedingRecord

@Database(
    entities = [Bird::class, Cage::class, FeedingRecord::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun birdDao(): BirdDao
    abstract fun cageDao(): CageDao
    abstract fun feedingDao(): FeedingDao

    companion object {
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE birds ADD COLUMN motherId INTEGER")
                db.execSQL("ALTER TABLE birds ADD COLUMN fatherId INTEGER")
                db.execSQL("ALTER TABLE birds ADD COLUMN expectedHatchDate INTEGER")
                db.execSQL("ALTER TABLE birds ADD COLUMN cheekColor INTEGER NOT NULL DEFAULT -3104")
                db.execSQL("ALTER TABLE birds ADD COLUMN throatColor INTEGER NOT NULL DEFAULT -10665929")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bird_nursery_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
