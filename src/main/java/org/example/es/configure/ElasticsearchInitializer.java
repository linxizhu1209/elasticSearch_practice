package org.example.es.configure;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ElasticsearchInitializer {

    private final ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void init() throws IOException {
        boolean exists = elasticsearchClient.indices()
                .exists(e -> e.index("questions"))
                .value();

        if(!exists) {
            elasticsearchClient.indices().create(c -> c
                    .index("questions")
                    .mappings(m -> m
                            .properties("title", p -> p.text(t -> t))
                            .properties("content", p -> p.text(t -> t))
                            .properties("createdAt", p -> p.text(d -> d))
                            .properties("comments", p -> p.nested(n -> n
                                    .properties("author", a -> a.keyword(k -> k))
                                    .properties("text", t -> t.text(tt -> tt))
                                    .properties("createdAt", cd -> cd.date(dd -> dd))
                            ))
                    )
            );
        }
    }
}
