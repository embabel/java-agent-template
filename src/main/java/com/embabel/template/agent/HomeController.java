package com.embabel.template.agent;

import com.embabel.agent.core.*;
import com.embabel.agent.domain.io.UserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HomeController {
    @Autowired
    private AgentPlatform agentPlatform;

    @RequestMapping("/")
    public String home() {
        Agent agent = agentPlatform.agents().getFirst();
        AgentProcess agentProcess = agentPlatform.runAgentFrom(
                agent,
                ProcessOptions.getDEFAULT(),
                Map.of(
                        "user_input", new UserInput("hello world", Instant.now())
                )
        );
        ReviewedStory reviewedStory = ((ReviewedStory) agentProcess.lastResult());
        return reviewedStory.getContent();
    }
}
