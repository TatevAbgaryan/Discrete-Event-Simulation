package graduation.tatev.myapplication.dao_implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import graduation.tatev.myapplication.ConnectionService;
import graduation.tatev.myapplication.components.GraphEdge;
import graduation.tatev.myapplication.dao.GraphDao;
import graduation.tatev.myapplication.dao.TerminalDao;

/**
 * Created by Tatka on 5/21/2016.
 */
public class MsSqlGraphDao implements GraphDao {
    private final Connection connection;

    MsSqlGraphDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(GraphEdge graphEdge) throws SQLException {
        String sql = "INSERT INTO Graph VALUES(?, ?, ?)";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, graphEdge.getSourceTerminal().getTerminalID());
        stm.setInt(2, graphEdge.getTargetTerminal().getTerminalID());
        stm.setInt(3, graphEdge.getWeight());
        stm.executeUpdate();
    }

    @Override
    public GraphEdge read(int fistTerminalId, int secondTerminalId) throws SQLException {
        return null;
    }

    @Override
    public void update(GraphEdge graphEdge) throws SQLException {
        String sql = "UPDATE Graph SET EdgeWeight = ? where SourceTerminalId =  ? and TargetTerminalId =  ?";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, graphEdge.getWeight());
        stm.setInt(2, graphEdge.getSourceTerminal().getTerminalID());
        stm.setInt(3, graphEdge.getTargetTerminal().getTerminalID());
        stm.executeUpdate();
    }

    @Override
    public void delete(int fistTerminalId, int secondTerminalId) throws SQLException {

    }

    @Override
    public void deleteAll() throws SQLException {
        String sql = "TRUNCATE TABLE Graph";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.executeUpdate();
    }

    @Override
    public List<GraphEdge> getGraph() throws SQLException {
        TerminalDao terminalDao = ConnectionService.getTerminalDao();
        String sql = "SELECT * FROM Graph;";
        PreparedStatement stm = connection.prepareStatement(sql);
        ResultSet rs = stm.executeQuery();
        List<GraphEdge> list = new ArrayList<>();
        while (rs.next()) {
            GraphEdge graphEdge = new GraphEdge();
            graphEdge.setSourceTerminal(terminalDao.read(rs.getInt("SourceTerminalId")));
            graphEdge.setTargetTerminal(terminalDao.read(rs.getInt("TargetTerminalId")));
            list.add(graphEdge);
        }
        return list;
    }
}
