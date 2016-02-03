package com.example.tatev.truckworkmodel.rowmappers;

/**
 * Created by tatev on 03.02.2016.
 */

import com.example.tatev.truckworkmodel.components.Truck;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class TruckMapper implements RowMapper<Truck> {
    public Truck mapRow(ResultSet rs, int rowNum) throws SQLException {
        Truck student = new Truck();
        student.setTruckID(rs.getInt("TruckID"));
        student.setStartTime(rs.getDate("StartTime"));
        student.setRecoveryTime(rs.getDate("RecoveryTime"));
        student.setArrivalTime(rs.getDate("ArrivalTime"));
        student.setSpeed(rs.getInt("Speed"));
        return student;
    }
}