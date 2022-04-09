package fr.oupson.foochat.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Message {
    public Message() {

    }

    public Message(long date, String mac, String texte, byte[] image, boolean mine) {
        this.date = date;
        this.mac = mac;
        this.texte = texte;
        this.image = image;
        this.mine = mine;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "mac")
    public String mac;

    @ColumnInfo(name = "mine")
    public boolean mine;

    @ColumnInfo(name = "date")
    public long date; //Y a pas un type Date normalement ?

    @ColumnInfo(name = "texte")
    public String texte;

    @ColumnInfo(name = "image")
    public byte[] image;  //Le type sera quoi ??
}
