package com.embabel.coding;

import com.embabel.agent.api.annotation.*;
import com.embabel.agent.api.common.LlmReference;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.library.code.SoftwareProject;
import com.embabel.agent.spi.ToolGroupResolver;
import com.embabel.coding.tools.api.ApiReference;
import com.embabel.coding.tools.git.RepositoryReferenceProvider;
import com.embabel.coding.tools.jvm.ClassGraphApiReferenceExtractor;
import com.embabel.common.ai.model.LlmOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Agent(description = "Agent that creates other agents")
public class Tyrell {

    private final Config config;
    private final ToolGroupResolver toolGroupResolver;

    private final List<LlmReference> references = new LinkedList<>();

    private final SoftwareProject softwareProject = new SoftwareProject(System.getProperty("user.dir"));

    public Tyrell(Config config, ToolGroupResolver toolGroupResolver) {
        this.config = config;
        this.toolGroupResolver = toolGroupResolver;

        var embabelApiReference = new ApiReference(
                new ClassGraphApiReferenceExtractor().fromProjectClasspath(
                        "embabel-agent",
                        Set.of("com.embabel.agent"),
                        Set.of()),
                100);
        var examplesReference = RepositoryReferenceProvider.create()
                .cloneRepository("https://github.com/embabel/embabel-agent-examples.git");
        references.add(embabelApiReference);
        references.add(examplesReference);
        references.add(softwareProject);
    }


    @ConfigurationProperties(prefix = "embabel.tyrell")
    public record Config(LlmOptions codingLlm) {
    }

    public record AgentCreationRequest(String pkg, String purpose) {
    }

    /**
     * The agent will have been created to fulfill this summary.
     *
     * @param summary
     */
    public record AgentCreationResult(String summary) {
    }

    public record ToolGroups(List<String> toolGroups) {
    }

    @Action
    AgentCreationRequest requestAgentDetails() {
        return WaitFor.formSubmission(
                "Please enter the package and purpose of the new agent",
                AgentCreationRequest.class);
    }

    @Action
    @AchievesGoal(
            description = "New agent has been created",
            export = @Export(remote = true, startingInputTypes = {AgentCreationRequest.class}))
    public AgentCreationResult createAgent(AgentCreationRequest request, OperationContext embabel) {
        return embabel.ai()
                .withLlm(config.codingLlm)
                .withReferences(references)
                .withTemplate("coding/creator")
                .createObject(
                        AgentCreationResult.class,
                        Map.of(
                                "thisProject", softwareProject.getName().replace("-", "_"),
                                "package", request.pkg(),
                                "purpose", request.purpose(),
                                "toolGroups", toolGroupResolver.availableToolGroups()
                        ));
    }
}
