package com.example.accounting.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "amount")
    private BigDecimal amount;
    
    @Column(name = "div")
    private String div; // 입금/출금 구분

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "description")
    private String description;

    @Column(name = "classify_status")
    private String classifyStatus;

}

