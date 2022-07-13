package com.coherentsolutions.by.max.sir.androidtrainingtasks.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.Pet
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.User
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.utils.CategoryConverter
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.utils.StringListConverter
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.utils.TagConverter

@Database(
    entities = [User::class, Pet::class],
    version = 4,
    exportSchema = true
)
@TypeConverters(CategoryConverter::class, TagConverter::class, StringListConverter::class)
abstract class UserDatabase: RoomDatabase() {
    abstract val userDao: UserDao
    abstract val petsDao: PetsDao


    companion object {
        const val DATABASE_TAG = "database.Database"

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("BEGIN TRANSACTION;")
                database.execSQL("ALTER TABLE `pets_table` RENAME TO `old_pets_table`")
                database.execSQL("CREATE TABLE IF NOT EXISTS `pets_table` (`category` TEXT, `id` INTEGER NOT NULL, `name` TEXT NOT NULL, `photoUrls` TEXT NOT NULL, `status` TEXT NOT NULL, `tags` TEXT NOT NULL, `petId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);")
                database.execSQL("INSERT INTO `pets_table` (category,id,name,photoUrls,status,tags,petId) SELECT category,id,name,photoUrls,status,tags,petId FROM `old_pets_table`;")
                database.execSQL("DROP TABLE `old_pets_table`;")
                database.execSQL("COMMIT;")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("BEGIN TRANSACTION;")
                database.execSQL("ALTER TABLE `pets_table` RENAME TO `old_pets_table`")
                database.execSQL(" CREATE TABLE IF NOT EXISTS `pets_table` (`category` TEXT, `id` INTEGER NOT NULL, `name` TEXT, `photoUrls` TEXT NOT NULL, `status` TEXT NOT NULL, `tags` TEXT NOT NULL, `petId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);")
                database.execSQL("INSERT INTO `pets_table` (category,id,name,photoUrls,status,tags,petId) SELECT category,id,name,photoUrls,status,tags,petId FROM `old_pets_table`;")
                database.execSQL("DROP TABLE `old_pets_table`;")
                database.execSQL("COMMIT;")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("BEGIN TRANSACTION;")
                database.execSQL("ALTER TABLE `pets_table` RENAME TO `old_pets_table`;")
                database.execSQL("CREATE TABLE `pets_table` (`category` TEXT, `id` INTEGER NOT NULL, `name` TEXT, `photoUrls` TEXT, `status` TEXT NOT NULL, `tags` TEXT, `petId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);")
                database.execSQL("INSERT INTO `pets_tabl` (category,id,name,photoUrls,status,tags,petId) SELECT category,id,name,photoUrls,status,tags,petId FROM `old_pets_table`;")
                database.execSQL("DROP TABLE `old_pets_table`;")
                database.execSQL("COMMIT;")
            }
        }

        @Volatile
        private var INSTANCE: UserDatabase? = null
        fun getInstance(context: Context): UserDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "user_database"
                    )
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}