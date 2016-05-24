package graduation.tatev.myapplication.dao_implementation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import graduation.tatev.myapplication.ConnectionService;
import graduation.tatev.myapplication.components.Container;
import graduation.tatev.myapplication.components.Truck;
import graduation.tatev.myapplication.dao.ContainerDao;
import graduation.tatev.myapplication.dao.TerminalDao;

/**
 * Created by Tatka on 5/21/2016.
 */
public class MsSqlContainerDao implements ContainerDao {
    private final Connection connection;

    MsSqlContainerDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Container container) throws SQLException {
        String sql = "INSERT INTO Container VALUES(?, ?, ?, ? , ?, ?)";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setDate(1, new Date(container.getReadyTime().getTime()));
        stm.setInt(2, container.getDepartureTerminal().getTerminalID());
        stm.setInt(3, container.getDestinationTerminal().getTerminalID());
        stm.setInt(4, container.getQuantity());
        stm.setDate(5, new java.sql.Date(container.getRecoveryTime().getTime()));
        stm.setDate(6, new java.sql.Date(container.getArrivalTime().getTime()));
        stm.executeUpdate();
    }

    @Override
    public Truck raed(Date ReadyTime, int initialTerminalId, int destinationTerminalId) throws SQLException {
        return null;
    }

    @Override
    public void update(Container container) throws SQLException {
        String sql = "UPDATE Container SET RecoveryTime = ?, ArrivalTime = ? where DestinationTerminalID =  ? and DepartureTerminalID=?";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setDate(1, new java.sql.Date(container.getRecoveryTime().getTime()));
        stm.setDate(2, new java.sql.Date(container.getArrivalTime().getTime()));
        //stm.setDate(3, new java.sql.Date(container.getReadyTime().getTime()));
        stm.setInt(3, container.getDestinationTerminal().getTerminalID());
        stm.setInt(4, container.getDepartureTerminal().getTerminalID());
        stm.executeUpdate();
    }

    @Override
    public void delete(Date ReadyTime, int initialTerminalId, int destinationTerminalId) throws SQLException {

    }

    @Override
    public List<Container> getContainers() throws SQLException {
        TerminalDao terminalDao = ConnectionService.getTerminalDao();
        String sql = "SELECT * FROM Container;";
        PreparedStatement stm = connection.prepareStatement(sql);
        ResultSet rs = stm.executeQuery();
        List<Container> list = new ArrayList<>();
        while (rs.next()) {
            Container container = new Container();
            container.setDepartureTerminal(terminalDao.read(rs.getInt("DepartureTerminalID")));
            container.setDestinationTerminal(terminalDao.read(rs.getInt("DestinationTerminalID")));
            container.setReadyTime(rs.getDate("ReadyTime"));
            container.setQuantity(rs.getInt("Quantity"));
            list.add(container);
        }
        return list;
    }

}
