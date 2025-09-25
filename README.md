<img align="left" src="https://github.com/embabel/embabel-agent/blob/main/embabel-agent-api/images/315px-Meister_der_Weltenchronik_001.jpg?raw=true" width="180">

![Build](https://github.com/embabel/java-agent-template/actions/workflows/maven.yml/badge.svg)

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/apache%20tomcat-%23F8DC75.svg?style=for-the-badge&logo=apache-tomcat&logoColor=black)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![ChatGPT](https://img.shields.io/badge/chatGPT-74aa9c?style=for-the-badge&logo=openai&logoColor=white)
![JSON](https://img.shields.io/badge/JSON-000?logo=json&logoColor=fff)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)

&nbsp;&nbsp;&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;

# Generated Agent Project

Starting point for your own agent development using the [Embabel framework](https://github.com/embabel/embabel-agent).

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

## A2A support

Embabel integrates with Google A2a. See [A2A integration](docs/a2a.md).

## Contributors

[![Embabel contributors](https://contrib.rocks/image?repo=embabel/java-agent-template)](https://github.com/embabel/java-agent-template/graphs/contributors)

