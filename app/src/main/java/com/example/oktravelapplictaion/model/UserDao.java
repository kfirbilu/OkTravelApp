package com.example.oktravelapplictaion.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface UserDao {

    @Query("select * from User")
    List<User> getAll();

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertAll(User... users);

    @Delete
    void delete(User user);
}
