package com.embabel.template.agent;

import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.testing.unit.FakeOperationContext;
import com.embabel.agent.testing.unit.FakePromptRunner;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WriteAndReviewAgentTest {

    @Test
    void testWriteAndReviewAgent() {
        var context = FakeOperationContext.create();
        var promptRunner = (FakePromptRunner) context.promptRunner();
        context.expectResponse(new WriteAndReviewAgent.Story("One upon a time Sir Galahad . . "));

        var agent = new WriteAndReviewAgent(200, 400);
        var story = agent.craftStory(new UserInput("Tell me a story about a brave knight", Instant.now()), context.ai());

        var prompt = promptRunner.getLlmInvocations().getFirst().getMessages().getFirst().getContent();
        assertTrue(prompt.contains("knight"), "Expected prompt to contain 'knight'");

    }

    @Test
    void testReview() {
        var agent = new WriteAndReviewAgent(200, 400);
        var userInput = new UserInput("Tell me a story about a brave knight", Instant.now());
        var story = new WriteAndReviewAgent.Story("Once upon a time, Sir Galahad...");
        var context = FakeOperationContext.create();
        context.expectResponse("A thrilling tale of bravery and adventure!");
        var review = agent.reviewStory(userInput, story, context.ai());
        var llmInvocation = context.getLlmInvocations().getFirst();
        var prompt = llmInvocation.getMessages().getFirst().getContent();
        assertTrue(prompt.contains("knight"), "Expected prompt to contain 'knight'");
        assertTrue(prompt.contains("review"), "Expected prompt to contain 'review'");
    }

}