package com.example.oktravelapplictaion.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.oktravelapplictaion.MyApplication;

@Database(entities = {User.class, Post.class, Location.class}, version = 42)

abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract PostDao postDao();
    public abstract LocationDao locationDao();
}
public class AppLocalDb{
    static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepository.class,
                    "travelapp.db")
                    .fallbackToDestructiveMigration()
                    .build();
}