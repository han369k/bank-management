package com.ispan.bankmanagement.loan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, String> {

    // 查詢全部資料
    // findAll + By(條件開始) + Order By CreateTime(按時間排序) + Desc(降序排列)
    List<LoanApplication> findAllByOrderByCreateTimeDesc();

    // 依照status查詢
    // find By Status + By + Order By CreateTime + Desc
    List<LoanApplication> findByStatusInOrderByCreateTimeDesc(List<LoanStatus> statusList);

}
