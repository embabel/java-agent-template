/*
 * Copyright 2024-2025 Embabel Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.embabel.template.agent;

import com.embabel.agent.api.annotation.*;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.domain.library.HasContent;
import com.embabel.agent.prompt.persona.Persona;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;
import com.embabel.common.ai.model.LlmOptions;
import com.embabel.common.core.types.Timestamped;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

abstract class Personas {
    static final RoleGoalBackstory WRITER = RoleGoalBackstory
            .withRole("Creative Storyteller")
            .andGoal("Write engaging and imaginative stories")
            .andBackstory("Has a PhD in French literature; used to work in a circus");

    static final Persona REVIEWER = new Persona(
            "Media Book Review",
            "New York Times Book Reviewer",
            "Professional and insightful",
            "Help guide readers toward good stories"
    );
}


@Agent(description = "Generate a story based on user input and review it")
@Profile("!test")
public class WriteAndReviewAgent {

    public record Story(String text) {
    }

    public record ReviewedStory(
            Story story,
            String review,
            Persona reviewer
    ) implements HasContent, Timestamped {

        @Override
        @NonNull
        public Instant getTimestamp() {
            return Instant.now();
        }

        @Override
        @NonNull
        public String getContent() {
            return String.format("""
                            # Story
                            %s
                            
                            # Review
                            %s
                            
                            # Reviewer
                            %s, %s
                            """,
                    story.text(),
                    review,
                    reviewer.getName(),
                    getTimestamp().atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))
            ).trim();
        }
    }

    @State
    interface Stage {
    }
    
    record Properties(
            int storyWordCount,
            int reviewWordCount
    ) {
    }

    private final Properties properties;

    WriteAndReviewAgent(
            @Value("${storyWordCount:100}") int storyWordCount,
            @Value("${reviewWordCount:100}") int reviewWordCount
    ) {
        this.properties = new Properties(storyWordCount, reviewWordCount);
    }


    @Action
    AssessStory craftStory(UserInput userInput, Ai ai) {
        var draft = ai
                // Higher temperature for more creative output
                .withLlm(LlmOptions
                        .withAutoLlm() // You can also choose a specific model or role here
                        .withTemperature(.7)
                )
                .withPromptContributor(Personas.WRITER)
                .createObject(String.format("""
                                Craft a short story in %d words or less.
                                The story should be engaging and imaginative.
                                Use the user's input as inspiration if possible.
                                If the user has provided a name, include it in the story.
                                
                                # User input
                                %s
                                """,
                        properties.storyWordCount,
                        userInput.getContent()
                ).trim(), Story.class);
        return new AssessStory(userInput, draft, properties);
    }

    /**
     * We prompt the user for this
     *
     * @param comments
     */
    record HumanFeedback(String comments) {
    }

    private record AssessmentOfHumanFeedback(boolean acceptable) {
    }

    @State
    record AssessStory(UserInput userInput, Story story, Properties properties) implements Stage {

        @Action
        HumanFeedback getFeedback() {
            return WaitFor.formSubmission("""
                            Please provide feedback on the story
                            %s
                            """.formatted(story.text),
                    HumanFeedback.class);
        }

        @Action
        Stage assess(HumanFeedback feedback, Ai ai) {
            var assessment = ai.withDefaultLlm().createObject("""
                                    Based on the following human feedback, determine if the story is acceptable.
                                    Return true if the story is acceptable, false otherwise.
                            
                                    # Story
                                    %s
                            
                                    # Human feedback
                                    %s
                            
                            
                                    Return your answer as a JSON object with a single field "acceptable" set to true or false.
                            """.formatted(
                            story.text(),
                            feedback.comments
                    )
                    , AssessmentOfHumanFeedback.class);
            if (assessment.acceptable) {
                return new Done(userInput, story, properties);
            } else {
                return new ReviseStory(userInput, story, feedback, properties);
            }
        }

    }

    @State
    record ReviseStory(UserInput userInput, Story story, HumanFeedback humanFeedback,
                       Properties properties) implements Stage {

        @Action
        AssessStory reviseStory(Ai ai) {
            var draft = ai
                    // Higher temperature for more creative output
                    .withLlm(LlmOptions
                            .withAutoLlm() // You can also choose a specific model or role here
                            .withTemperature(.7)
                    )
                    .withPromptContributor(Personas.WRITER)
                    .createObject(String.format("""
                                    Revise a short story in %d words or less.
                                    The story should be engaging and imaginative.
                                    Use the user's input as inspiration if possible.
                                    If the user has provided a name, include it in the story.
                                    
                                    # User input
                                    %s
                                    
                                    # Previous story
                                    %s
                                    
                                    # Revision instructions
                                    %s
                                    """,
                            properties.storyWordCount,
                            userInput.getContent(),
                            story.text(),
                            humanFeedback.comments
                    ).trim(), Story.class);
            return new AssessStory(userInput, draft, properties);
        }
    }

    @State
    record Done(UserInput userInput, Story story, Properties properties) implements Stage {

        @AchievesGoal(
                description = "The story has been crafted and reviewed by a book reviewer",
                export = @Export(remote = true, name = "writeAndReviewStory"))
        @Action
        ReviewedStory reviewStory(Ai ai) {
            var review = ai
                    .withAutoLlm()
                    .withPromptContributor(Personas.REVIEWER)
                    .generateText(String.format("""
                                    You will be given a short story to review.
                                    Review it in %d words or less.
                                    Consider whether or not the story is engaging, imaginative, and well-written.
                                    Also consider whether the story is appropriate given the original user input.
                                    
                                    # Story
                                    %s
                                    
                                    # User input that inspired the story
                                    %s
                                    """,
                            properties.reviewWordCount,
                            story.text(),
                            userInput.getContent()
                    ).trim());

            return new ReviewedStory(
                    story,
                    review,
                    Personas.REVIEWER
            );
        }
    }


}