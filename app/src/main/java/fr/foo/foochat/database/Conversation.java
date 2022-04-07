package fr.foo.foochat.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Conversation {
    @PrimaryKey
    public int adresseMac;

    @ColumnInfo(name = "titreConv")
    public String titreConv;

    @ColumnInfo(name = "icone")
    public String icone;
}
