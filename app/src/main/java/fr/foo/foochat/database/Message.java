package fr.foo.foochat.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Message {
    public Message() {

    }

    public Message(long date, String texte, byte[] image) {
        this.date = date;
        this.texte = texte;
        this.image = image;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "date")
    public long date; //Y a pas un type Date normalement ?

    @ColumnInfo(name = "texte")
    public String texte;

    @ColumnInfo(name = "image")
    public byte[] image;  //Le type sera quoi ??
}
