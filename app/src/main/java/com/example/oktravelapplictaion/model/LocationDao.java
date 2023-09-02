package com.example.oktravelapplictaion.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface LocationDao {

    @Query("select * from Location")
    List<Location> getAll();

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertAll(Location... locations);

    @Delete
    void delete(Location location);
}
