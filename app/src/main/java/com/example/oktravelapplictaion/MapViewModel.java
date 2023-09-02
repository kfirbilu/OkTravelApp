package com.example.oktravelapplictaion;

import androidx.lifecycle.ViewModel;
import com.example.oktravelapplictaion.model.Location;
import java.util.List;

public class MapViewModel extends ViewModel {
    public List<Location> db_locations;

    public List<Location> getDb_locations() {
        return db_locations;
    }

    public void setDb_locations(List<Location> db_locations) {
        this.db_locations = db_locations;
    }
}
