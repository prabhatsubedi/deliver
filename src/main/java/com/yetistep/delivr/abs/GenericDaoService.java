package com.yetistep.delivr.abs;

import org.hibernate.Session;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Surendra
 * Date: 2013-09-07
 * Time: 22:43
 * To change this template use File | Settings | File Templates.
 */
public interface GenericDaoService<K extends Serializable, T> {

    public T find(K id) throws SQLException;

    public List<T> findAll() throws SQLException;

    public Boolean save(T value) throws SQLException;

    public Boolean update(T value) throws SQLException;

    public Boolean delete(T value) throws SQLException;

    public Session getCurrentSession() throws Exception;
}
