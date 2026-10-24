package com.example.fadchena.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ScanRecordEntity::class, CommunityReportEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FadChenaDatabase : RoomDatabase() {
    abstract fun scanRecordDao(): ScanRecordDao
    abstract fun communityReportDao(): CommunityReportDao

    companion object {
        @Volatile
        private var INSTANCE: FadChenaDatabase? = null

        fun getInstance(context: Context): FadChenaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FadChenaDatabase::class.java,
                    "fadchena_security.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Preload realistic community alerts and reported cases for Bangladesh
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).communityReportDao()
                                dao.insertAll(InitialCommunitySeedData.reports)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

object InitialCommunitySeedData {
    val reports = listOf(
        CommunityReportEntity(
            id = "seed_1",
            target = "01889213456",
            category = "PHONE",
            description = "bKash হেড অফিসের পরিচয় দিয়ে কল করে অ্যাকাউন্টে ভুলবশত টাকা গেছে দাবি করে পিন ও ওটিপি চাওয়া হয়।",
            lossAmountBdt = 12500.0,
            evidenceNote = "অডিও রেকর্ডিং ও এসএমএস সংরক্ষিত আছে। সাইবার পুলিশে অভিযোগ দায়ের করা হয়েছে।",
            status = "VERIFIED",
            reportCount = 14,
            reportedAt = System.currentTimeMillis() - 86400000L * 2,
            moderatorNote = "বিটিআরসি ও কাস্টমার কেয়ার নিশ্চিত করেছে এটি ব্যক্তিগত নম্বর এবং ভুয়া কল।"
        ),
        CommunityReportEntity(
            id = "seed_2",
            target = "http://bkash-eid-bonus2026.top",
            category = "URL",
            description = "ঈদের স্পেশাল ২৫০০ টাকা বোনাস দাবি করে লিংকটি মেসেঞ্জারে ছড়ানো হচ্ছে। ক্লিক করলে ভুয়া bKash লগইন পেজে পিন চাওয়া হয়।",
            lossAmountBdt = 0.0,
            evidenceNote = "ফিশিং ডোমেন স্ক্রিনশট ও এইচটিএমএল ফর্ম সংরক্ষিত।",
            status = "VERIFIED",
            reportCount = 38,
            reportedAt = System.currentTimeMillis() - 86400000L * 1,
            moderatorNote = "ফিশিং ডাটাবেজে অন্তর্ভুক্ত করা হয়েছে। ডোমেইনটি রিপোর্ট করা হয়েছে।"
        ),
        CommunityReportEntity(
            id = "seed_3",
            target = "01799441122",
            category = "PHONE",
            description = "চাকরির নিয়োগপত্র দেওয়ার নামে ৫০০০ টাকা মেডিকেল ও ইউনিফর্ম ফি দাবি করেছে।",
            lossAmountBdt = 5000.0,
            evidenceNote = "বিকাশ লেনদেন ট্রানজেকশন আইডি: 9HJ87KD2L",
            status = "VERIFIED",
            reportCount = 8,
            reportedAt = System.currentTimeMillis() - 86400000L * 3,
            moderatorNote = "ভূয়া নিয়োগ চক্রের নম্বর হিসেবে একাধিক ভুক্তভোগী রিপোর্ট করেছে।"
        ),
        CommunityReportEntity(
            id = "seed_4",
            target = "BD Fashion Hub 24",
            category = "JOB_OR_SHOP",
            description = "ফেসবুক পেজে কাশ্মীরি শাল ও ঘড়ির অর্ডারে ২০০ টাকা অগ্রিম ডেলিভারি চার্জ নিয়ে পেজ থেকে ব্লক করে দেয়।",
            lossAmountBdt = 200.0,
            evidenceNote = "চ্যাট স্ক্রিনশট ও বিকাশ নাম্বার দেওয়া আছে।",
            status = "UNDER_REVIEW",
            reportCount = 3,
            reportedAt = System.currentTimeMillis() - 86400000L * 4,
            moderatorNote = "অতিরিক্ত স্ক্রিনশট যাচাই করা হচ্ছে।"
        ),
        CommunityReportEntity(
            id = "seed_5",
            target = "https://bit.ly/daraz-free-giftcard-bd",
            category = "URL",
            description = "দারাজ ৫০% ডিসকাউন্ট গিফট কার্ডের নামে টেলিগ্রাম গ্রুপে লিংক দেওয়া হয় যাতে ম্যালিসিয়াস apk ডাউনলোড হয়।",
            lossAmountBdt = 0.0,
            evidenceNote = "লিংক শর্টনারের পেছনে লুকানো apk ডাউনলোড ইউআরএল চিহ্নিত।",
            status = "VERIFIED",
            reportCount = 22,
            reportedAt = System.currentTimeMillis() - 86400000L * 5,
            moderatorNote = "মারাত্মক স্পাইওয়্যার এপিকে যুক্ত থাকায় সতর্কবার্তা দেওয়া হলো।"
        )
    )
}
