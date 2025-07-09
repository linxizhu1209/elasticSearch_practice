package org.example.es.Service;

import lombok.RequiredArgsConstructor;
import org.example.es.Entity.Question;
import org.example.es.Repository.JpaQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionJpaService {
    private final JpaQuestionRepository jpaQuestionRepository;

    public List<Question> searchByTitle(String keyword){
        return jpaQuestionRepository.findByTitleContaining(keyword);
    }
}
