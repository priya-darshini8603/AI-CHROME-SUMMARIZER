package com.summary.assistance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class SummaryService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    public SummaryService(WebClient.Builder webBuilder, ObjectMapper objectMappers) {
        this.webClient = webBuilder.build();
        this.objectMapper = objectMappers;
    }


    public String processContent(Summary research) {
        try {
            String prompt = buildPrompt(research);
            Map < String, Object > request = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
                )
            );
            String apiUrl = geminiApiUrl + "?key=" + geminiApiKey; 
            System.out.println("Final API URL: " + apiUrl);
            System.out.println("Request Body: " + objectMapper.writeValueAsString(request));
            String response = webClient.post()
                .uri(apiUrl) 
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            System.out.println("Raw API Response: " + response); // Add this to debug response

            return extractText(response);
        } catch (Exception e) {
            System.err.println("API Error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    private String extractText(String reponse) {
        try {
            GeminiResponse geminiResponse = objectMapper.readValue(reponse, GeminiResponse.class);
            if (geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {

                GeminiResponse.Candidate fisrt = geminiResponse.getCandidates().get(0);
                if (fisrt.getContent() != null && fisrt.getContent().getParts() != null && !fisrt.getContent().getParts().isEmpty()) {
                    return fisrt.getContent().getParts().get(0).getText();
                }
            }
            return "no content found in response";

        } catch (Exception e) {
            return " Error Parsing" + e.getMessage();
        }

    }


    private String buildPrompt(Summary research) {
        StringBuilder promt = new StringBuilder();

        switch (research.getOperation()) {
            case "summarize":
                promt.append("Provide a clear and concise summary of the following content in a few sentences:\n");
                break;
            case "suggest":
                promt.append("Suggest follow-up research questions, related topics, or next learning steps based on the following content:\n");
                break;
            default:
                throw new IllegalArgumentException("Unknown Operation: " + research.getOperation());
        }

        promt.append(research.getContent());

        return promt.toString();

    }
}