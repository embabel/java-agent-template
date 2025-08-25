package com.embabel.joke.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.Export;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.prompt.persona.Persona;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;
import com.embabel.common.ai.model.LlmOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import java.util.List;

abstract class JokePersonas {
    static final RoleGoalBackstory JOKE_WRITER = RoleGoalBackstory
            .withRole("Professional Comedian")
            .andGoal("Write funny and clever jokes that make people laugh")
            .andBackstory("Spent 20 years doing stand-up comedy in clubs around the world");

    static final Persona JOKE_CRITIC = Persona.create(
            "Comedy Critic",
            "Late Night Comedy Show Writer",
            "Sharp, witty, and constructive",
            "Evaluate jokes for humor, delivery, and audience appeal"
    );
}

record Joke(String setup, String punchline) {
}

record JokeIdea(String topic, String style) {
}

record RefinedJoke(
        Joke originalJoke,
        Joke improvedJoke,
        String critique
) {
}

@Agent(description = "Generate jokes based on user input and refine them")
@Profile("!test")
class JokeAgent {

    private final int maxJokeLength;

    JokeAgent(@Value("${maxJokeLength:150}") int maxJokeLength) {
        this.maxJokeLength = maxJokeLength;
    }

    @AchievesGoal(
            description = "A joke has been generated and refined based on feedback",
            export = @Export(remote = true, name = "generateAndRefineJoke"))
    @Action
    RefinedJoke refineJoke(UserInput userInput, Joke joke, OperationContext context) {
        var critique = context
                .ai()
                .withAutoLlm()
                .withPromptContributor(JokePersonas.JOKE_CRITIC)
                .generateText(String.format("""
                                Evaluate this joke. Provide constructive feedback on:
                                - How funny it is
                                - Whether the punchline lands well
                                - If it's appropriate for the intended audience
                                - Suggestions for improvement
                                
                                Keep your critique brief and actionable.
                                
                                # The Joke
                                Setup: %s
                                Punchline: %s
                                
                                # Original user request
                                %s
                                """,
                        joke.setup(),
                        joke.punchline(),
                        userInput.getContent()
                ).trim());

        var improvedJoke = context
                .ai()
                .withLlm(LlmOptions.withAutoLlm().withTemperature(.8))
                .withPromptContributor(JokePersonas.JOKE_WRITER)
                .createObject(String.format("""
                                Based on this critique, create an improved version of the joke.
                                Keep the total length under %d characters.
                                
                                # Original Joke
                                Setup: %s
                                Punchline: %s
                                
                                # Critique
                                %s
                                """,
                        maxJokeLength,
                        joke.setup(),
                        joke.punchline(),
                        critique
                ).trim(), Joke.class);

        return new RefinedJoke(joke, improvedJoke, critique);
    }

    @Action
    Joke writeJoke(JokeIdea jokeIdea, OperationContext context) {
        return context.ai()
                .withLlm(LlmOptions.withAutoLlm().withTemperature(.9))
                .withPromptContributor(JokePersonas.JOKE_WRITER)
                .createObject(String.format("""
                                Write a joke about: %s
                                Style: %s
                                
                                The joke should have a clear setup and punchline.
                                Keep the total length under %d characters.
                                Make it clever and funny.
                                """,
                        jokeIdea.topic(),
                        jokeIdea.style(),
                        maxJokeLength
                ).trim(), Joke.class);
    }

    @Action
    JokeIdea brainstormJoke(UserInput userInput, OperationContext context) {
        return context.ai()
                .withAutoLlm()
                .createObject(String.format("""
                                Based on the user's input, identify:
                                1. A topic for a joke (be specific)
                                2. A comedy style (e.g., pun, observational, one-liner, knock-knock, etc.)
                                
                                If the user didn't specify preferences, choose something appropriate and funny.
                                
                                # User input
                                %s
                                """,
                        userInput.getContent()
                ).trim(), JokeIdea.class);
    }
}