package com.embabel.joke.agent;

import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.testing.unit.FakeOperationContext;
import com.embabel.agent.testing.unit.FakePromptRunner;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JokeAgentTest {

    @Nested
    class BrainstormJokeTests {
        @Test
        void testBrainstormJokeWithSpecificTopic() {
            var context = FakeOperationContext.create();
            var expectedIdea = new JokeIdea("cats", "pun");
            context.expectResponse(expectedIdea);

            var agent = new JokeAgent(150);
            var result = agent.brainstormJoke(new UserInput("Tell me a joke about cats", Instant.now()), context);

            assertEquals("cats", result.topic());
            assertEquals("pun", result.style());
            
            var llmInvocation = context.getLlmInvocations().getFirst();
            assertTrue(llmInvocation.getPrompt().contains("cats"), "Expected prompt to contain 'cats'");
        }

        @Test
        void testBrainstormJokeWithNoSpecificTopic() {
            var context = FakeOperationContext.create();
            context.expectResponse(new JokeIdea("coffee", "observational"));

            var agent = new JokeAgent(150);
            var result = agent.brainstormJoke(new UserInput("Tell me a funny joke", Instant.now()), context);

            assertNotNull(result.topic());
            assertNotNull(result.style());
        }
    }

    @Nested
    class WriteJokeTests {
        @Test
        void testWriteJokeFromIdea() {
            var context = FakeOperationContext.create();
            var promptRunner = (FakePromptRunner) context.promptRunner();
            var expectedJoke = new Joke("Why don't cats play poker?", "Too many cheetahs!");
            context.expectResponse(expectedJoke);

            var agent = new JokeAgent(150);
            var jokeIdea = new JokeIdea("cats", "pun");
            var result = agent.writeJoke(jokeIdea, context);

            assertNotNull(result.setup());
            assertNotNull(result.punchline());
            assertEquals("Why don't cats play poker?", result.setup());
            assertEquals("Too many cheetahs!", result.punchline());

            var prompt = promptRunner.getLlmInvocations().getFirst().getPrompt();
            assertTrue(prompt.contains("cats"), "Expected prompt to contain topic");
            assertTrue(prompt.contains("pun"), "Expected prompt to contain style");
        }
    }

    @Nested
    class RefineJokeTests {
        @Test
        void testRefineJokeWithCritique() {
            var agent = new JokeAgent(150);
            var userInput = new UserInput("Tell me a joke about programming", Instant.now());
            var originalJoke = new Joke("Why do programmers prefer dark mode?", "Because light attracts bugs!");
            var context = FakeOperationContext.create();
            
            // First expectation for critique
            context.expectResponse("Good pun but could be more clever. Consider a twist on the setup.");
            
            // Second expectation for improved joke
            var improvedJoke = new Joke("Why do programmers hate nature?", "It has too many bugs!");
            context.expectResponse(improvedJoke);
            
            var result = agent.refineJoke(userInput, originalJoke, context);
            
            assertNotNull(result.critique());
            assertNotNull(result.improvedJoke());
            assertEquals(originalJoke, result.originalJoke());
            assertTrue(result.critique().contains("pun"), "Expected critique to mention pun");
            
            var llmInvocations = context.getLlmInvocations();
            assertEquals(2, llmInvocations.size());
            
            // Check critique prompt
            var critiquePrompt = llmInvocations.get(0).getPrompt();
            assertTrue(critiquePrompt.contains("Evaluate"), "Expected critique prompt to contain 'Evaluate'");
            assertTrue(critiquePrompt.contains(originalJoke.setup()), "Expected critique prompt to contain original setup");
            
            // Check improvement prompt
            var improvementPrompt = llmInvocations.get(1).getPrompt();
            assertTrue(improvementPrompt.contains("improved version"), "Expected improvement prompt to contain 'improved version'");
        }
    }

    @Test
    void testFullJokeGenerationFlow() {
        var agent = new JokeAgent(200);
        var userInput = new UserInput("I need a knock-knock joke", Instant.now());
        var context = FakeOperationContext.create();
        
        // Brainstorm
        context.expectResponse(new JokeIdea("doors", "knock-knock"));
        var jokeIdea = agent.brainstormJoke(userInput, context);
        
        // Write joke
        var joke = new Joke("Knock knock. Who's there? Interrupting cow.", "Interrupting cow w-- MOO!");
        context.expectResponse(joke);
        var writtenJoke = agent.writeJoke(jokeIdea, context);
        
        // Refine joke
        context.expectResponse("Classic format but timing could be better emphasized.");
        context.expectResponse(new Joke("Knock knock. Who's there? Interrupting cow.", "Interrupt-- MOOOOO!"));
        var refinedJoke = agent.refineJoke(userInput, writtenJoke, context);
        
        assertNotNull(refinedJoke.improvedJoke());
        assertEquals(4, context.getLlmInvocations().size());
    }
}