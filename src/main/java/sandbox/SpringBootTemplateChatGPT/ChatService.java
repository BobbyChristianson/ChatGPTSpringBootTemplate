package sandbox.SpringBootTemplateChatGPT;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpHeaders httpHeaders;
    private static final int delay = 5000; // Wait for 5 seconds before retrying
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static List<JSONObject> conversationHistory = new LinkedList<>();


    public String chat(String userInput) {
        // Add user's message to the conversation history
        addUserMessage(userInput);

        try {
            // Create the request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo"); // Replace with your custom model ID
            requestBody.put("messages", new JSONArray(conversationHistory));
            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), httpHeaders);
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, request, String.class);

            // Get response and add reply to the conversation history
            JSONObject jsonResponse = new JSONObject(response.getBody());
            String chatGptReply = jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            addAssistantMessage(chatGptReply);

            return chatGptReply;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    conversationHistory.remove(conversationHistory.size()-1);
                    return "Error: Retry interrupted.";
                }
            } else {
                e.printStackTrace();
                conversationHistory.remove(conversationHistory.size()-1);
                return "Error: " + e.getMessage();
            }
        }
        conversationHistory.remove(conversationHistory.size()-1);
        return "Error: Could not get a response from ChatGPT after retries.";
    }

    private static void addSystemMessage(String content) {
        JSONObject message = new JSONObject();
        message.put("role", "system");
        message.put("content", content);
        conversationHistory.add(message);
    }

    private static void addUserMessage(String content) {
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", content);
        conversationHistory.add(message);
    }

    private static void addAssistantMessage(String content) {
        JSONObject message = new JSONObject();
        message.put("role", "assistant");
        message.put("content", content);
        conversationHistory.add(message);
    }

}
