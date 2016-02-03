package com.example.tatev.truckworkmodel.jdbstemplates;

import com.example.tatev.truckworkmodel.components.Truck;
import com.example.tatev.truckworkmodel.dao.TruckDao;
import com.example.tatev.truckworkmodel.rowmappers.TruckMapper;

import org.springframework.jdbc.core.JdbcTemplate;
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
        this.dataSource = ds;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(int speed, Date startTime, Date recoveryTime, Date arrivalTime) {
        String SQL = "insert into Truck (startTime, recoveryTime, arrivalTime,speed) values (?, ?)";

        jdbcTemplateObject.update( SQL, startTime, recoveryTime, arrivalTime,speed);
    }

      @Override
    public Truck getTruck(int id) {
          String SQL = "select * from Truck where id = ?";
          Truck truck = jdbcTemplateObject.queryForObject(SQL,
                  new Object[]{id}, new TruckMapper());
          return  truck;
    }

    @Override
    public List<Truck> listTrucks() {
        String SQL = "select * from Truck";
        List <Truck> trucks = jdbcTemplateObject.query(SQL,
                new TruckMapper());
        return trucks;
    }

    @Override
    public void delete(Integer id) {
        String SQL = "delete from Truck where id = ?";
        jdbcTemplateObject.update(SQL, id);
     }

}
