package com.danlevy.todo.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Random;

@Entity(tableName = "todo")
public class Tache {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @NonNull
    @ColumnInfo(name = "info")
    private String info;

    @ColumnInfo(name = "icon")
    private int icon;

    @ColumnInfo(name = "chosen")
    private boolean chosen;

    public Tache(@NonNull String title, @NonNull String info) {
        this.title = title;
        this.info = info;
        this.chosen = false;
        this.icon = new Random().nextInt(5);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getInfo() {
        return info;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public boolean getChosen() {
        return chosen;
    }
}
