package fr.oupson.foochat.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ConvDao {
    @Query("SELECT * FROM Conversation")
    List<Conversation> getAll();

    @Query("SELECT * FROM Conversation")
    LiveData<List<Conversation>> getAllAsync();

    @Query("SELECT * FROM CONVERSATION WHERE adresseMac = :mac")
    LiveData<Conversation> getConv(String mac);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertConv(Conversation... conversations);

    @Update
    void updateConv(Conversation... conversations);

    @Delete
    void delete(Conversation conversation);


}
