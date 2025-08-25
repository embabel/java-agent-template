package com.embabel.template.agent;

import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.testing.unit.FakeOperationContext;
import com.embabel.agent.testing.unit.FakePromptRunner;
import com.embabel.agent.testing.unit.UnitTestUtils;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WriteAndReviewAgentTest {
    
    @Test
    void testWriteAndReviewAgent() {
        var context = FakeOperationContext.create();
        var promptRunner = (FakePromptRunner) context.promptRunner();
        context.expectResponse(new Story("One upon a time Sir Galahad . . "));

        var agent = new WriteAndReviewAgent(200, 400);
        agent.craftStory(new UserInput("Tell me a story about a brave knight", Instant.now()), context);

        String prompt = promptRunner.getLlmInvocations().getFirst().getPrompt();
        assertTrue(prompt.contains("knight"), "Expected prompt to contain 'knight'");

    }

    @Test
    void testReview() {
        var agent = new WriteAndReviewAgent(200, 400);
        var userInput = new UserInput("Tell me a story about a brave knight", Instant.now());
        var story = new Story("Once upon a time, Sir Galahad...");
        var context = FakeOperationContext.create();
        context.expectResponse("A thrilling tale of bravery and adventure!");
        agent.reviewStory(userInput, story, context);
        var llmInvocation = context.getLlmInvocations().getFirst();
        assertTrue(llmInvocation.getPrompt().contains("knight"), "Expected prompt to contain 'knight'");
        assertTrue(llmInvocation.getPrompt().contains("review"), "Expected prompt to contain 'review'");
    }

}