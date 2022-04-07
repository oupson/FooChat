package fr.foo.foochat.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
