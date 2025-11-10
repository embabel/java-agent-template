package com.embabel.template.injected;

import com.embabel.agent.api.common.Ai;
import jakarta.validation.constraints.Pattern;
import org.springframework.stereotype.Component;

/**
 * Demonstrate injection of Embabel's OperationContext into a Spring component.
 *
 * @param ai Embabel AI helper, injected by Spring
 */
@Component
public record InjectedDemo(Ai ai) {

    /**
     * Demonstrates use of JSR-380 validation annotations on record fields
     * to constrain generated content.
     */
    public record Animal(
            String name,
            @Pattern(regexp = ".*ox.*", message = "Species must contain 'ox'")
            String species) {
    }

    public Animal inventAnimal() {
        return ai
                .withDefaultLlm()
                .withId("invent-animal")
                .createObject("""
                                You just woke up in a magical forest.
                                Invent a fictional animal.
                                The animal should have a name and a species.
                                """,
                        Animal.class);
    }
}
