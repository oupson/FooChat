package fr.foo.foochat.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Message.class, Conversation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MessageDao msgDao();

    public abstract ConvDao convDao();
}
