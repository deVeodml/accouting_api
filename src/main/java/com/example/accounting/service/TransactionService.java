
package com.example.accounting.service;

import com.example.accounting.model.Transaction;
import com.example.accounting.repository.TransactionRepository;
import com.example.accounting.utils.RuleParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionMapper transactionMapper) {
        this.transactionMapper = transactionMapper;
    }

    public List<Transaction> process(MultipartFile csvFile, MultipartFile rulesFile) throws Exception {
        List<Transaction> results   = new ArrayList<>();
        ObjectMapper mapper         = new ObjectMapper();
        Map<String, Object> rules   = mapper.readValue(rulesFile.getInputStream(), Map.class);
        List<String> lines          = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))
                                        .lines().skip(1).collect(Collectors.toList());

        for (String line : lines) {
            String[] cols       = line.split(",");
            Transaction tx      = new Transaction();
            
            String date             = cols[0].trim();                   // 거래일시
            String description      = cols[1].trim();                   // 적요
            BigDecimal inAmount     = new BigDecimal(cols[2].trim());   // 입금액
            BigDecimal outAmount    = new BigDecimal(cols[3].trim());   // 출금액
            BigDecimal balance      = new BigDecimal(cols[4].trim());   // 거래후잔액
            
            tx.setTransactionDate(LocalDate.parse(date));
            tx.setDescription(description);
            tx.setBalance(balance); // 거래후잔액 set

            if (inAmount.compareTo(BigDecimal.ZERO) > 0) {
                tx.setAmount(inAmount); // 금액set
                tx.setDiv("0"); // 입출금 구분
            } else if (outAmount.compareTo(BigDecimal.ZERO) > 0) {
                tx.setAmount(outAmount); // 금액set
                tx.setDiv("1"); // 출금 구분
            }

            Map<String, String> classification = RuleParser.classify(description, rules); // 분류 규칙에 따른 적요별 계정 분류
            
            if (classification != null) {
                tx.setCompanyId(classification.get("companyId"));
                tx.setCategoryId(classification.get("categoryId"));
                tx.setCategoryName(classification.get("categoryName"));
                tx.setClassifyStatus("분류완료");
            } else {
                tx.setClassifyStatus("미분류");
            }
            transactionMapper.save(tx);
            results.add(tx);
        }

        return results;
    }

    public List<Map<String, Object>> getTransactionsByCompany(String companyId) {
        return transactionMapper.findByCompanyId(companyId).stream()
                .map(tx -> Map.of(
                        "date", tx.getTransactionDate(),
                        "description", tx.getDescription(),
                        "amount", tx.getAmount(),
                        "div", tx.getDiv(),
                        "balance", tx.getBalance(),
                        "company_id", tx.getCompanyId(),
                        "transaction_id", tx.getTransactionId(),
                        "category_id", tx.getCategoryId(),
                        "category_name", tx.getCategoryName(),
                        "classify_status", tx.getClassifyStatus()
                )).collect(Collectors.toList());
    }
}
