
package com.example.accounting.utils;

import java.util.List;
import java.util.Map;

public class RuleParser {
    public static Map<String, String> classify(String description, Map<String, Object> rules) {
        
        List<Map<String, Object>> companies = (List<Map<String, Object>>) rules.get("companies");
        
        for (Map<String, Object> company : companies) {
            
            List<Map<String, Object>> categories = (List<Map<String, Object>>) company.get("categories");
            
            for (Map<String, Object> category : categories) {
                
                List<String> keywords = (List<String>) category.get("keywords");
                
                for (String keyword : keywords) {
                    if (description.contains(keyword)) {
                        return Map.of(
                                "companyId", (String) company.get("company_id"),
                                "categoryId", (String) category.get("category_id"),
                                "categoryName", (String) category.get("category_name")
                        );
                    }
                }
            }
        }
        return null;
    }
}
