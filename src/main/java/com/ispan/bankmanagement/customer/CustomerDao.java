package com.ispan.bankmanagement.customer;

import com.ispan.bankmanagement.common.util.ConnUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) — 資料庫操作員
 * 這個 class 「只負責跟資料庫說話」。
 * 裡面有 4 個方法，對應 CRUD：新增(C)、查詢(R)、修改(U)、刪除(D)
 */
public class CustomerDao {

    // ==================== C：新增顧客 ====================
    /**
     * 把一個 Customer 物件的資料，寫入資料庫 CUSTOMER 資料表。
     * @return true = 新增成功, false = 失敗
     */
    public boolean insertCustomer(Customer c) {
        String sql = "INSERT INTO CUSTOMER "
                   + "(customer_id, id_number, name, date_of_birth, nationality, "
                   + " address, phone, email, password_hash, income, credit_score, "
                   + " created_at, updated_at, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATETIME(), SYSDATETIME(), ?)";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getCustomerId());
            pstmt.setString(2, c.getIdNumber());
            pstmt.setString(3, c.getName());
            pstmt.setDate(4, c.getDateOfBirth());
            pstmt.setString(5, c.getNationality() != null ? c.getNationality() : "Taiwan");
            pstmt.setString(6, c.getAddress());
            pstmt.setString(7, c.getPhone());
            pstmt.setString(8, c.getEmail());
            pstmt.setString(9, "default_pwd_hash"); // 實際上應該要 hash 過
            pstmt.setBigDecimal(10, c.getIncome() != null ? c.getIncome() : BigDecimal.ZERO);
            pstmt.setInt(11, c.getCreditScore());
            pstmt.setString(12, "Active"); // 新顧客預設為 Active

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[CustomerDao][insertCustomer] 錯誤：" + e.getMessage());
            return false;
        }
    }

    // ==================== R：查詢全部顧客 ====================
    /**
     * 撈出 CUSTOMER 資料表所有的資料，回傳一個 List（清單）。
     */
    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT customer_id, id_number, name, date_of_birth, "
                   + "address, phone, email, income, credit_score, status "
                   + "FROM CUSTOMER ORDER BY created_at DESC";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Customer c = mapResultSet(rs);
                list.add(c);
            }

        } catch (SQLException e) {
            System.err.println("[CustomerDao][findAll] 錯誤：" + e.getMessage());
        }
        return list;
    }

    // ==================== R：用身分證字號查詢單一顧客 ====================
    /**
     * 用身分證字號搜尋特定顧客。
     * @param idNumber 身分證字號，例如 "A123456789"
     * @return 找到回傳 Customer 物件, 找不到回傳 null
     */
    public Customer findByIdNumber(String idNumber) {
        String sql = "SELECT customer_id, id_number, name, date_of_birth, "
                   + "address, phone, email, income, credit_score, status "
                   + "FROM CUSTOMER WHERE id_number = ?";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[CustomerDao][findByIdNumber] 錯誤：" + e.getMessage());
        }
        return null; // 找不到就回傳 null
    }

    // ==================== U：修改顧客狀態 ====================
    /**
     * 更新特定顧客的 status 欄位（例如：Active → Frozen）。
     * @param customerId 顧客系統代號，例如 "C2026001"
     * @param newStatus  新狀態，例如 "Frozen" 或 "Active"
     * @return true = 修改成功
     */
    public boolean updateStatus(String customerId, String newStatus) {
        String sql = "UPDATE CUSTOMER SET status = ?, updated_at = SYSDATETIME() "
                   + "WHERE customer_id = ?";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setString(2, customerId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[CustomerDao][updateStatus] 錯誤：" + e.getMessage());
            return false;
        }
    }

    // ==================== D：刪除顧客 ====================
    /**
     * 從資料庫刪除一筆顧客資料。
     * 注意：實際銀行系統通常不真的刪除，改 status；這裡為了示範 CRUD 提供真刪除。
     * @param customerId 顧客系統代號
     * @return true = 刪除成功
     */
    public boolean deleteCustomer(String customerId) {
        String sql = "DELETE FROM CUSTOMER WHERE customer_id = ?";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[CustomerDao][deleteCustomer] 錯誤：" + e.getMessage());
            return false;
        }
    }

    // ==================== 私有輔助方法 ====================
    /**
     * 把 ResultSet 的一行，裝進一個 Customer 物件。
     * 這樣每個查詢方法就不用重複寫一樣的轉換程式碼。
     */
    private Customer mapResultSet(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getString("customer_id"));
        c.setIdNumber(rs.getString("id_number"));
        c.setName(rs.getString("name"));
        c.setDateOfBirth(rs.getDate("date_of_birth"));
        c.setAddress(rs.getString("address"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setIncome(rs.getBigDecimal("income"));
        c.setCreditScore(rs.getInt("credit_score"));
        c.setStatus(rs.getString("status"));
        return c;
    }
}
