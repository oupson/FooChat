package fr.foo.foochat.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM Message")
    List<Message> getAll();

    @Insert
    void insertMessage(Message... messages);

    @Update
    void updateMessages(Message... messages);

    @Delete
    void delete(Message message);
}
