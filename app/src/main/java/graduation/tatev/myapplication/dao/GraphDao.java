package graduation.tatev.myapplication.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import graduation.tatev.myapplication.components.GraphEdge;
import graduation.tatev.myapplication.components.Terminal;

/**
 * Created by Tatka on 5/21/2016.
 */
public interface GraphDao {

    public void insert(GraphEdge graphEdge) throws SQLException;

    public GraphEdge read(int fistTerminalId, int secondTerminalId) throws SQLException;

    public void update(GraphEdge graphEdge) throws SQLException;

    public void delete(int fistTerminalId, int secondTerminalId) throws SQLException;

    public void deleteAll() throws SQLException;

    public List<GraphEdge> getGraph() throws SQLException;
}
