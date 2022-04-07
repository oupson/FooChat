package fr.foo.foochat;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("fr.foo.foochat", appContext.getPackageName());
    }

    //Test de création de BD
    public void creationBD() {
        AppDatabase db = Room.databaseBuilder(getApplicatonContext(), AppDatabase.class, "Ma DB").build();
    }
}

@Entity
public class Message {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "date")
    public String date; //Y a pas un type Date normalement ?

    @ColumnInfo(name = "texte")
    public String texte;

    @ColumnInfo(name = "image")
    public String image;  //Le type sera quoi ??
}

@Entity
public class Conversation {
    @PrimaryKey
    public int adresseMac;

    @ColumnInfo(name = "titreConv")
    public String titreConv;

    @ColumnInfo(name = "icone")
    public String icone;
}

@Dao
public interface MessageDao {
    @Query("SELECT * FROM Message")
    List<Message> getAll();

    @Insert
    void insertMessage(Message... messages);

    @Update
    void updateMessages(Message... messages)

    @Delete
    void delete(Message message);
}

@Dao
public interface ConvDao {
    @Query("SELECT * FROM Conversation")
    List<Conversation> getAll();

    @Insert
    void insertConv(Conversation... conversations);

    @Update
    void updateConv(Conversation... conversations)

    @Delete
    void delete(Conversation conversation);
}

@Database(entities = {Message.class, Conversation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MessageDao msgDao();
    public abstract ConvDao convDao();
}

