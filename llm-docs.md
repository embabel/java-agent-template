# Embabel LLM Integration Guide

This guide describes how to integrate Embabel with different LLM providers; by default Embabel runs with OpenAI
but you can configure other providers.

Embabel uses Spring AI and you can find detailed documentation [here](https://docs.spring.io/spring-ai/reference/api/index.html).

# Amazon Bedrock

To run this project with Amazon Bedrock, you need to configure the following:

## Prerequisites

- AWS account with appropriate permissions
- AWS CLI installed and configured with your credentials

## 1. Amazon Bedrock Console Setup

1. Go to AWS Console -> Amazon Bedrock Console
2. Navigate to **Configure and Learn** → **Model Access**
3. Click **Modify Model Access**
4. Enable **Claude 3.5 Sonnet**

## 2. Add Dependency

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.embabel.agent</groupId>
    <artifactId>embabel-agent-starter-bedrock</artifactId>
    <version>${embabel-agent.version}</version>
</dependency>
```

## 3. Environment Variables

Set these environment variables to any value:

```bash
export ANTHROPIC_API_KEY=null
export OPENAI_API_KEY=null
```

## 4. Application Configuration

Add the following to your `application.properties`:

```properties
embabel.models.default-llm=us.anthropic.claude-3-5-sonnet-20240620-v1:0
embabel.agent.platform.ranking.llm=us.anthropic.claude-3-5-sonnet-20240620-v1:0
spring.ai.bedrock.anthropic.chat.inference-profile-id=us.anthropic.claude-3-5-sonnet-20240620-v1:0
spring.profiles.active=starwars,bedrock
```

## 5. Run the Project

After configuration, run the project using:

```bash
./scripts/shell.sh
```

## Additional Links

Spring AI and Amazon Bedrock [documentation]([https://docs.spring.io/spring-ai/reference/api/bedrock.html])

