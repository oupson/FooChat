package fr.foo.foochat.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM Message ORDER BY date ASC")
    List<Message> getAll();


    @Query("SELECT * FROM Message WHERE mac = :mac ORDER BY date ASC")
    LiveData<List<Message>> getAllObservable(String mac);

    @Insert
    void insertMessage(Message... messages);

    @Update
    void updateMessages(Message... messages);

    @Delete
    void delete(Message message);
}
