
package com.example.accounting.controller;

import com.example.accounting.model.Transaction;
import com.example.accounting.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounting")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processTransactions(@RequestParam("csvFile") MultipartFile csvFile,
                                                 @RequestParam("rulesFile") MultipartFile rulesFile) throws Exception {
        List<Transaction> transactions = transactionService.process(csvFile, rulesFile);
        return ResponseEntity.ok("총 " + transactions.size() + "건 처리 완료");
    }

    @GetMapping("/records")
    public ResponseEntity<List<Map<String, Object>>> getRecordsByCompany(@RequestParam("companyId") String companyId) {
        List<Map<String, Object>> result = transactionService.getTransactionsByCompany(companyId);
        return ResponseEntity.ok(result);
    }
}
