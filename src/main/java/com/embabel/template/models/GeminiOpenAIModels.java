package com.embabel.template.models;

import com.embabel.agent.config.models.OpenAiChatOptionsConverter;
import com.embabel.common.ai.model.Llm;
import com.embabel.common.ai.prompt.CurrentDate;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
@ConditionalOnProperty("GEMINI_API_KEY")
public class GeminiOpenAIModels {
    static final Logger logger = LoggerFactory.getLogger(GeminiOpenAIModels.class);

    private static final String GEMINI_2_5_FLASH = "gemini-2.5-flash";
    private static final String GEMINI_2_0_FLASH = "gemini-2.0-flash";
    private static final String GEMINI_PROVIDER = "Google";

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;
    @Value("${GEMINI_BASE_URL:https://generativelanguage.googleapis.com/v1beta/openai}")
    private String baseUrl;
    @Value("${GEMINI_CHAT_COMPLETIONS:/chat/completions}")
    private String completionsPath;
    @Value("${GEMINI_EMBEDDINGS:/embeddings}")
    private String embeddingsPath;
    @Autowired
    private ObservationRegistry observationRegistry;

    private OpenAiApi openAiApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(geminiApiKey)
                .completionsPath(completionsPath)
                .embeddingsPath(embeddingsPath)
                .build();
    }

    private OpenAiChatModel chatModelOf(String model) {
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi())
                .retryTemplate(RetryUtils.DEFAULT_RETRY_TEMPLATE)
                .defaultOptions(
                        OpenAiChatOptions.
                                builder()
                                .model(model)
                                .build())
                .observationRegistry(observationRegistry)
                .build();
    }

    @Bean
    public Llm gemini_2_0_flash() {
        logger.info("Registering Gemini Open AI compatible model: {}", GEMINI_2_0_FLASH);
        return new Llm(GEMINI_2_0_FLASH,
                GEMINI_PROVIDER,
                chatModelOf(GEMINI_2_0_FLASH),
                OpenAiChatOptionsConverter.INSTANCE,
                LocalDate.now(),
                List.of(new CurrentDate()),
                null);
    }

    @Bean
    public Llm gemini_2_5_flash() {
        logger.info("Registering Gemini Open AI compatible model: {}", GEMINI_2_5_FLASH);
        return new Llm(GEMINI_2_5_FLASH,
                GEMINI_PROVIDER,
                chatModelOf(GEMINI_2_5_FLASH),
                OpenAiChatOptionsConverter.INSTANCE,
                LocalDate.now(),
                List.of(new CurrentDate()),
                null);
    }
}