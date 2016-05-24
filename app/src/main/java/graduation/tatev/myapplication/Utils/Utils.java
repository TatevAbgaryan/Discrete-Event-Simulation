package graduation.tatev.myapplication.Utils;

import android.util.Log;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import graduation.tatev.myapplication.ConnectionService;
import graduation.tatev.myapplication.components.Container;
import graduation.tatev.myapplication.components.GraphEdge;
import graduation.tatev.myapplication.components.Terminal;
import graduation.tatev.myapplication.components.Truck;
import graduation.tatev.myapplication.dao.ContainerDao;
import graduation.tatev.myapplication.dao.DaoFactory;
import graduation.tatev.myapplication.dao.GraphDao;
import graduation.tatev.myapplication.dao.TerminalDao;
import graduation.tatev.myapplication.dao.TruckDao;
import graduation.tatev.myapplication.events.ContainerReadyEvent;

/**
 * Created by Tatka on 4/16/2016.
 */
public class Utils {
    private static int MID_SPEED = 80;
//   private static boolean isGraphSame = false;
//    private static boolean isFirstRun = true;
//
//    public static boolean isGraphSame() {
//        return isGraphSame;
//    }

    public static void getEventList(List<ContainerReadyEvent> eventList, Date simulationStartDate) {
        ContainerDao containerDao = ConnectionService.getContainerDao();
        try {
            List<Container> containers = containerDao.getContainers();
            for (Container container : containers) {
                ContainerReadyEvent containerReadyEvent = new ContainerReadyEvent();
                containerReadyEvent.setStartTime(container.getReadyTime());
                containerReadyEvent.setDestinationTerminal(container.getDestinationTerminal());
                containerReadyEvent.setDepartureTerminal(container.getDepartureTerminal());
                containerReadyEvent.setStartTime(container.getReadyTime());
                containerReadyEvent.setConteiner(container);
                eventList.add(containerReadyEvent);
            }
            simulationStartDate.setTime(eventList.get(0).getStartTime().getTime());
            for (ContainerReadyEvent event : eventList) {
                if (event.getStartTime().before(simulationStartDate))
                    simulationStartDate.setTime(event.getStartTime().getTime());
            }
        } catch (SQLException e) {
            Log.d("excaptionGettingEvents", e.toString());
        }
    }

    public static void fillAllPairShortestPaths(double[][] shortesDurations, int[][] shortestRoutes, List<GraphEdge> graphEdges) {
        Connection connection = ConnectionService.getConnection();
        TerminalDao terminalDao = ConnectionService.getTerminalDao();
        GraphDao graphDao = ConnectionService.getGraphDao();
        final List<Terminal> terminals;
        try {
            graphEdges.addAll(graphDao.getGraph());
            terminals = terminalDao.getAllTerminals();
            // fill matrixes with initial values
            for (int i = 0; i < shortestRoutes.length; i++)
                for (int j = 0; j < shortestRoutes.length; j++) {
                    shortestRoutes[i][j] = j;
                    GraphEdge graphEdge = new GraphEdge(terminals.get(i), terminals.get(j));
                    if (i == j) {
                        shortesDurations[i][j] = 0;
                    } else if (graphEdges.contains(graphEdge)) {
                        CallableStatement callableStatement =
                                connection.prepareCall("{? = call dbo.getDistance(?,?)}");
                        callableStatement.setInt(2, terminals.get(i).getTerminalID());
                        callableStatement.setInt(3, terminals.get(j).getTerminalID());
                        callableStatement.registerOutParameter(1, Types.INTEGER);
                        callableStatement.execute();

                        shortesDurations[i][j] = (callableStatement.getInt(1) / MID_SPEED) * 60   //  hour
                                + terminals.get(i).getDuration() + terminals.get(j).getDuration(); // minutes
                        graphEdge.setWeight((int) shortesDurations[i][j]);
                        graphDao.update(graphEdge);
                    } else {
                        shortesDurations[i][j] = Double.POSITIVE_INFINITY;
                    }

                }

        } catch (SQLException e) {
            Log.d("excaptionGettingGraph", e.toString());
        }
        // all pair shortest paths Floyd-Warshal
        for (int i = 0; i < shortesDurations.length; i++) {
            // compute shortest paths using only 0, 1, ..., i as intermediate vertices
            for (int v = 0; v < shortesDurations.length; v++) {
                for (int w = 0; w < shortesDurations.length; w++) {
                    if (shortesDurations[v][w] > shortesDurations[v][i] + shortesDurations[i][w]) {
                        shortesDurations[v][w] = shortesDurations[v][i] + shortesDurations[i][w];
                        shortestRoutes[v][w] = shortestRoutes[v][i];
                    }
                }
            }
        }
    }

    public static void updateContainer(final List<ContainerReadyEvent> events) {
       final ContainerDao containerDao = ConnectionService.getContainerDao();
        new Thread() {
            public void run() {
                try {
                    for (ContainerReadyEvent event : events) {
                        containerDao.update(event.getConteiner());
                    }
                } catch (SQLException e) {
                    Log.d("exceptionUpdatContainer", e.toString());
                }
            }
        }.start();
    }

    public static void saveTrucks(final List<Truck> trucks) {
        new Thread() {
            public void run() {
                TruckDao truckDao = ConnectionService.getTruckDao();

                try {
                    truckDao.deleteAll();
                    for (Truck truck : trucks) {
                        truckDao.insert(truck);
                    }
                } catch (SQLException e) {
                    Log.d("exceptionSaveinTrucks", e.toString());
                }

            }
        }.start();

    }

}
