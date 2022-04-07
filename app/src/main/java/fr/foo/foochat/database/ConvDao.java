package fr.foo.foochat.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ConvDao {
    @Query("SELECT * FROM Conversation")
    List<Conversation> getAll();

    @Insert
    void insertConv(Conversation... conversations);

    @Update
    void updateConv(Conversation... conversations);

    @Delete
    void delete(Conversation conversation);
}
