package com.ispan.bankmanagement.stock.dao;

import com.ispan.bankmanagement.util.ConnUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BaseDao {

    @FunctionalInterface
    public interface RowMapper<T> {
        T mapRow(ResultSet rs) throws SQLException;
    }

    //<T>宣告使用泛型 回傳List<T>
    //Object... params 能夠接受不限定數量的參數 對應SQL語句的「?」(包括0個 也就是只填sql跟rowMapper兩個參數)
    protected <T> List<T> Query(String sql, RowMapper<T> rowMapper, Object... params) throws SQLException {
        List<T> list = new ArrayList<>();
        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    list.add(rowMapper.mapRow(rs));
                }
            }
        }
        return list;
    }

    //增刪改都通用 因為他們都是用PreparedStatement.executeUpdate()操作SQL
    //回傳int 表示異動了多少筆資料
    protected int Update(Connection conn,String sql, Object... params) throws SQLException {

        try (
             PreparedStatement ps = conn.prepareStatement(sql);) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            return ps.executeUpdate();
        }
    }
}
