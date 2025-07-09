package com.summary.assistance;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Data
@CrossOrigin(origins = "*")
@RequestMapping("/api/summary")
public class SummaryController {
    private final SummaryService service;
    @PostMapping("/process")
    public ResponseEntity<String> processContent(@RequestBody Summary research){
        String result = service.processContent(research);
        return ResponseEntity.ok(result);
    }
}
