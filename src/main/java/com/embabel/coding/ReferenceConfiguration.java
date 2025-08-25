package com.embabel.coding;

import com.embabel.agent.rag.RagService;
import com.embabel.agent.rag.lucene.LuceneRagService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReferenceConfiguration {

    @Bean
    public RagService ragService() {
        return new LuceneRagService(
                "code ref",
                "code description"
        );
    }
}
