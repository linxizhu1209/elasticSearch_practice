package org.example.es.Repository;

import org.example.es.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {


    List<Question> findByTitleContaining(String keyword);
}
