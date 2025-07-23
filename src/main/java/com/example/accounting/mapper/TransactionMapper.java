
package com.example.accounting.repository;

import com.example.accounting.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Mapper
public interface TransactionMapper{
    
    @Insert("""
        INSERT INTO transactions (
            transaction_date, description, div, amt, balance,
            company_id, category_id, category_name, classify_status
        ) VALUES (
            #{transactionDate}, #{description}, #{div}, #{amount}, #{balance},
            #{companyId}, #{categoryId}, #{categoryName}, #{classifyStatus}
        )
    """)
    void save(Transaction transaction);

    @Select("""
        SELECT
            transaction_id AS transactionId,
            transaction_date AS transactionDate,
            description,
            div,
            amt AS amount,
            balance,
            company_id AS companyId,
            category_id AS categoryId,
            category_name AS categoryName,
            classify_status AS classifyStatus
        FROM transactions
        WHERE company_id = #{companyId}
        ORDER BY transaction_date DESC
    """)
    List<TransactionRecordDto> findByCompanyId(@Param("companyId") String companyId);
}
