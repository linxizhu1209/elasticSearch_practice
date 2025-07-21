package org.example.es.Controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.example.es.Data.QuestionDocument;
import org.example.es.Service.QuestionDualIndexService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuestionTestController {

    private final QuestionDualIndexService service;

    @PostMapping("/test/save")
    public void save() throws IOException {
        service.saveSample();
    }

    @GetMapping("/test/search/nested")
    public List<QuestionDocument> nestedSearch(
            @RequestParam String author,
            @RequestParam String text) throws IOException {
        return service.searchNested(author, text);
    }


    @GetMapping("/test/search/object")
    public List<QuestionDocument> objectSearch(
            @RequestParam String author,
            @RequestParam String text
    ) throws IOException {
        return service.searchObject(author,text);
    }




}
