package com.spring.ai.demo;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AssistantController {

    private final ChatClient chatClient;

    @Value("classpath:system-message.txt")
    private Resource resource;

    @Value("classpath:/data/emails.json")
    private Resource emailsResource;

    private VectorStore vectorStore;

    private final EmbeddingClient embeddingClient;

    public AssistantController(ChatClient chatClient, EmbeddingClient embeddingClient) {
        this.chatClient = chatClient;
        this.embeddingClient = embeddingClient;
    }

    @PostMapping("/assistant/command")
    public String sendCommand(@RequestBody String message) {
        initVectorStore();

        var docs = getSimilarDocs(message);

        var systemMessage = new SystemPromptTemplate(resource)
                .createMessage(Map.of("emails", docs));

        var userMessage = new UserMessage(message);

        return chatResponseToString(chatClient.call(new Prompt(
                List.of(systemMessage, userMessage),
                OpenAiChatOptions.builder()
                        .withFunction("sendEmail")
                        .build())));
    }

    private void initVectorStore() {
        var reader = new JsonReader(emailsResource);
        var documents = reader.get();
        vectorStore = new SimpleVectorStore(embeddingClient);
        vectorStore.add(documents);
    }

    private String getSimilarDocs(String message) {
        var similarDocs = vectorStore.similaritySearch(message);
        return similarDocs.stream().map(Document::getContent).collect(Collectors.joining());
    }

    private String chatResponseToString(ChatResponse chatResponse) {
        return chatResponse.getResults().stream()
                .map(m -> m.getOutput().getContent())
                .toList()
                .toString();
    }
}
