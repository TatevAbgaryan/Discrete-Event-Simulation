package graduation.tatev.myapplication.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import graduation.tatev.myapplication.components.Container;
import graduation.tatev.myapplication.components.Truck;

/**
 * Created by Tatka on 5/21/2016.
 */
public interface ContainerDao {
    public void insert(Container container) throws SQLException;

    public Truck raed(Date readyTime,int initialTerminalId,int destinationTerminalId) throws SQLException;

    public void update(Container container) throws SQLException;

    public void delete(Date readyTime,int initialTerminalId,int destinationTerminalId) throws SQLException;

    public List<Container> getContainers() throws SQLException;

}
