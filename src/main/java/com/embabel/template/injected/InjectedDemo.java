package com.embabel.template.injected;

import com.embabel.agent.api.common.Ai;
import org.springframework.stereotype.Component;

/**
 * Demonstrate injection of Embabel's OperationContext into a Spring component.
 *
 * @param ai Embabel AI helper, injected by Spring
 */
@Component
public record InjectedDemo(Ai ai) {

    public record Animal(String name, String species) {
    }

    public Animal inventAnimal() {
        return ai
                .withDefaultLlm()
                .createObject("""
                                You just woke up in a magical forest.
                                Invent a fictional animal.
                                The animal should have a name and a species.
                                """,
                        Animal.class);
    }
}
