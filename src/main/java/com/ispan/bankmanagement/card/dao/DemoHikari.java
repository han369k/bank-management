package com.ispan.bankmanagement.card.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ispan.bankmanagement.util.ConnUtil;

public class DemoHikari {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sql="SELECT * from INFORMATION_SCHEMA.TABLES";
		
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				System.out.println(rs.getString("TABLE_NAME"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
