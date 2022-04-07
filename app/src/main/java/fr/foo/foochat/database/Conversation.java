package fr.foo.foochat.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Arrays;

@Entity
public class Conversation {
    public Conversation() {
    }

    public Conversation(String adresseMac, String titreConv, byte[] icone) {
        this.adresseMac = adresseMac;
        this.titreConv = titreConv;
        this.icone = icone;
    }

    @PrimaryKey
    @ColumnInfo(name = "adresseMac")
    @NonNull
    public String adresseMac;

    @ColumnInfo(name = "titreConv")
    public String titreConv;

    @ColumnInfo(name = "icone")
    public byte[] icone;

    @Override
    public String toString() {
        return "Conversation{" +
                "adresseMac='" + adresseMac + '\'' +
                ", titreConv='" + titreConv + '\'' +
                ", icone=" + Arrays.toString(icone) +
                '}';
    }
}
