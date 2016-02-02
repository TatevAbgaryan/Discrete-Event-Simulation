package com.example.tatev.truckworkmodel.dao;

import com.example.tatev.truckworkmodel.components.Truck;

import java.sql.Date;
import java.util.List;

import javax.sql.DataSource;

/**
 * Created by tatev on 02.02.2016.
 */
public interface TruckDao {

    public void setDataSource(DataSource ds);

    public void create(int speed, Date startTime, Date recoveryTime, Date arrivalTime);

    public void insert(Truck truck);

    public Truck getTruck(int id);

    public List<Truck> listTrucks();

    public void delete(Integer id);

}
