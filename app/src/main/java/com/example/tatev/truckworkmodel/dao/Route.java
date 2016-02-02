package com.example.tatev.truckworkmodel.dao;

import java.util.List;

import javax.sql.DataSource;

/**
 * Created by tatev on 02.02.2016.
 */
public interface Route {

    public void setDataSource(DataSource ds);

    public void create(int terminalID, int truckID, int sequance);

    public void insert(Route route);

    public Route getTerminal(int id);

    public List<Route> listTerminals();

    public void delete(Integer id);
}
