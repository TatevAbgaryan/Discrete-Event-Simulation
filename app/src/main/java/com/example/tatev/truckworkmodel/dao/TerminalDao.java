package com.example.tatev.truckworkmodel.dao;

import com.example.tatev.truckworkmodel.components.Terminal;

import java.util.List;

import javax.sql.DataSource;

/**
 * Created by tatev on 02.02.2016.
 */
public interface TerminalDao {

    public void setDataSource(DataSource ds);

    public void create(int duration,float latitude,float longitude);

    public void insert(Terminal terminal);

    public Terminal getTerminal(int id);

    public List<Terminal> listTerminals();

    public void delete(Integer id);
}
