package com.example.tatev.truckworkmodel.jdbstemplates;

import com.example.tatev.truckworkmodel.components.Truck;
import com.example.tatev.truckworkmodel.dao.TruckDao;
import java.sql.Date;
import java.util.List;
import javax.sql.DataSource;

/**
 * Created by tatev on 02.02.2016.
 */
public class TruckJDBSTemplate implements TruckDao {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    @Override
    public void setDataSource(DataSource ds) {

    }

    @Override
    public void create(int speed, Date startTime, Date recoveryTime, Date arrivalTime) {

    }

    @Override
    public void insert(Truck truck) {

    }

    @Override
    public Truck getTruck(int id) {
        return null;
    }

    @Override
    public List<Truck> listTrucks() {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }
}
