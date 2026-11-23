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
    version = 4,
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

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Старые color-колонки удаляются, добавляются генотипы.
                // Room сравнивает схему строго, поэтому таблицу пересоздаём.
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS birds_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        species TEXT NOT NULL,
                        birthDate TEXT NOT NULL,
                        arrivalDate TEXT NOT NULL,
                        weight TEXT NOT NULL,
                        sex TEXT NOT NULL,
                        diseases TEXT NOT NULL,
                        notes TEXT NOT NULL,
                        feedingIntervalHours INTEGER NOT NULL,
                        cageId INTEGER,
                        photoUri TEXT,
                        motherId INTEGER,
                        fatherId INTEGER,
                        expectedHatchDate INTEGER,
                        headGenotype TEXT NOT NULL,
                        breastGenotype TEXT NOT NULL,
                        bodyGenotype TEXT NOT NULL,
                        FOREIGN KEY(cageId) REFERENCES cages(id) ON UPDATE NO ACTION ON DELETE SET NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO birds_new (
                        id, name, species, birthDate, arrivalDate, weight, sex, diseases, notes,
                        feedingIntervalHours, cageId, photoUri, motherId, fatherId, expectedHatchDate,
                        headGenotype, breastGenotype, bodyGenotype
                    )
                    SELECT id, name, species, birthDate, arrivalDate, weight, sex, diseases, notes,
                        feedingIntervalHours, cageId, photoUri, motherId, fatherId, expectedHatchDate,
                        'RRoo', 'PP', 'GGoo'
                    FROM birds
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE birds")
                db.execSQL("ALTER TABLE birds_new RENAME TO birds")
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
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
