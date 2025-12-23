<img align="left" src="https://github.com/embabel/embabel-agent/blob/main/embabel-agent-api/images/315px-Meister_der_Weltenchronik_001.jpg?raw=true" width="180">

# Generated Agent Project

![Build](https://github.com/embabel/java-agent-template/actions/workflows/maven.yml/badge.svg)

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white) ![ChatGPT](https://img.shields.io/badge/chatGPT-74aa9c?style=for-the-badge&logo=openai&logoColor=white)

<br clear="left"/>

Starting point for your own agent development using the [Embabel framework](https://github.com/embabel/embabel-agent).

Uses Spring Boot 3.5.9 and Embabel 0.3.1.

Add your magic here!

Illustrates:

- An injected demo showing how any Spring component can be injected with an Embabel `Ai` instance to enable it to
  perform LLM operations.
- A simple agent
- Unit tests for an agent verifying prompts and hyperparameters

> For the Kotlin equivalent, see
> our [Kotlin agent template](https://github.com/embabel/kotlin-agent-template).

# Running

Run the shell script to start Embabel under Spring Shell:

```bash
./scripts/shell.sh
```

There is a single example
agent, [WriteAndReviewAgent](./src/main/java/com/embabel/template/agent/WriteAndReviewAgent.java).
It uses one LLM with a high temperature and creative persona to write a story based on your input,
then another LLM with a low temperature and different persona to review the story.

When the Embabel shell comes up, invoke the story agent like this:

```
x "Tell me a story about...[your topic]"
```

Try the following other shell commands:

- `demo`: Runs the same agent, invoked programmatically, instead of dynamically based on user input.
  See [DemoCommands.java](./src/main/java/com/embabel/template/DemoShell.java) for the
  implementation.
- `animal`:  Runs a simple demo using an Embabel injected `Ai` instance to call an LLM.
  See [InjectedDemo](./src/main/java/com/embabel/template/injected/InjectedDemo.java).

## Suggested Next Steps

To get a feel for working with Embabel, try the following:

- Modify the prompts in `WriteAndReviewAgent` and `InjectedDemo`.
- Experiment with different models and hyperparameters by modifying `withLlm` calls.
- Integrate your own services, injecting them with Spring. All Embabel `@Agent` classes are Spring beans.
- Run the tests with `mvn test` and modify them to experiment with prompt verification.

To see tool support, check out the more
complex [Embabel Agent API Examples](https://github.com/embabel/embabel-agent-examples) repository.

## Model support

Embabel integrates with any LLM supported by Spring AI.

See [LLM integration guide](docs/llm-docs.md) (work in progress).

Also see [Spring AI models](https://docs.spring.io/spring-ai/reference/api/index.html).

## Testing

This repository includes unit tests and integration tests demonstrating how to test Embabel agents.

### Running Tests

```bash
mvn test
```

### Unit Tests

Unit tests use Embabel's `FakeOperationContext` and `FakePromptRunner` to test agent actions in isolation without
calling actual LLMs.

See [WriteAndReviewAgentTest.java](./src/test/java/com/embabel/template/agent/WriteAndReviewAgentTest.java) for examples
of:

- Creating a fake context with `FakeOperationContext.create()`
- Setting up expected responses with `context.expectResponse()`
- Verifying prompt content contains expected values
- Inspecting LLM invocations via `promptRunner.getLlmInvocations()`

```java
var context = FakeOperationContext.create();
context.expectResponse(new Story("Once upon a time..."));

var story = agent.craftStory(userInput, context.ai());

var prompt = context.getLlmInvocations().getFirst().getMessages().getFirst().getContent();
assertTrue(prompt.contains("knight"));
```

### Integration Tests

Integration tests extend `EmbabelMockitoIntegrationTest` to test complete agent workflows under Spring Boot with a fully
configured `AgentPlatform`.

See [WriteAndReviewAgentIntegrationTest.java](./src/test/java/com/embabel/template/agent/WriteAndReviewAgentIntegrationTest.java)
for examples of:

- Mocking LLM responses with `whenCreateObject()` and `whenGenerateText()`
- Running complete agent workflows via `AgentInvocation`
- Verifying LLM calls and hyperparameters with `verifyCreateObjectMatching()` and `verifyGenerateTextMatching()`

```java
whenCreateObject(prompt -> prompt.contains("Craft a short story"), Story.class)
    .thenReturn(new Story("AI will transform our world..."));

var invocation = AgentInvocation.create(agentPlatform, ReviewedStory.class);
var result = invocation.invoke(input);

verifyCreateObjectMatching(
    prompt -> prompt.contains("Craft a short story"),
    Story.class,
    llm -> llm.getLlm().getTemperature() == 0.7
);
```

## Contributors

[![Embabel contributors](https://contrib.rocks/image?repo=embabel/java-agent-template)](https://github.com/embabel/java-agent-template/graphs/contributors)

