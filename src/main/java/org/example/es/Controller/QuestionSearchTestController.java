package org.example.es.Controller;

import lombok.RequiredArgsConstructor;
import org.example.es.Service.BulkInsertService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class QuestionSearchTestController {

    private final BulkInsertService bulkInsertService;

    @PostMapping("/test/bulk-insert")
    public String bulkInsert(@RequestParam(defaultValue = "100000") int count) throws IOException {
        System.out.println("==== 단건 저장 시작 ====");
        long start = System.currentTimeMillis();
        bulkInsertService.insertManyQuestions(count);
        long end = System.currentTimeMillis();
        System.out.println("단건 저장 소요 시간(ms): " + (end - start));

        System.out.println("==== Bulk 저장 시작 ====");
        long start2 = System.currentTimeMillis();
        bulkInsertService.insertManyBulk(count);
        long end2 = System.currentTimeMillis();
        System.out.println("Bulk 저장 소요 시간(ms): " + (end2 - start2));

        return count+"건 저장 완료";
    }


}
