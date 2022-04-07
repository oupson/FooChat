package fr.foo.foochat.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Message.class, Conversation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MessageDao msgDao();

    public abstract ConvDao convDao();

    private static AppDatabase INSTANCE = null;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "conv").build();
        }

        return INSTANCE;
    }
}
