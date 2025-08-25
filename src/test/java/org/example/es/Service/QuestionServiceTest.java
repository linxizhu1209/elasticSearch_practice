package org.example.es.Service;

import jakarta.transaction.Transactional;
import org.example.es.Data.QuestionDocument;
import org.example.es.Entity.Question;
import org.example.es.Repository.QuestionDocumentRepository;
import org.example.es.Repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class QuestionServiceTest {

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuestionDocumentRepository questionDocumentRepository;

    @Test
    void 질문_등록_성공() throws IOException {
        // given
        String title = "엘라스틱서치에 대하여";
        String content = "엘라스틱서치란 무엇인지 알려주세요.";

        // when
        Long id = questionService.createQuestion(title, content);

        //then
        Question q = questionRepository.findById(id).orElseThrow();
        assertEquals(title, q.getTitle());
        assertEquals(content, q.getContent());
    }

    @Test
    void 질문_등록_실패_예시() throws IOException {
        // given
        String title = "엘라스틱서치에 대하여";
        String content = "엘라스틱서치란 무엇인지 알려주세요.";

        // when
        Long id = questionService.createQuestion(title, content);

        //then
        Question q = questionRepository.findById(id).orElseThrow();
        assertEquals("엘라스틱서치!", q.getTitle());
    }

    @Test
    void 예외_발생_테스트(){
        String title = "";
        String content = "엘라스틱 서치는 무엇인가요";

        assertThrows(IllegalArgumentException.class, () -> {
            questionService.createQuestion(title, content);
        });
    }


    @Test
    void 제목으로_질문_검색() throws Exception {
        //given
        questionService.createQuestion("엘라스틱 서치란?", "검색엔진입니다");
        // 지연 방지 대기
        Thread.sleep(1000);
        //when
        List<QuestionDocument> results = questionDocumentRepository.searchByTitle("엘라스틱");

        //then
        assertFalse(results.isEmpty(), "검색 결과가 비어 있으면 안 됨");
        assertTrue(results.get(0).getTitle().contains("엘라스틱"));
    }


}