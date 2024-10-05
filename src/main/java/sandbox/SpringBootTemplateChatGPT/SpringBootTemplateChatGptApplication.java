package sandbox.SpringBootTemplateChatGPT;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SpringBootTemplateChatGptApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(SpringBootTemplateChatGptApplication.class, args);
//	}
private static final String API_KEY = System.getenv("OPENAI_KEY"); // Pull auth key from environment
	private static RestTemplate restTemplate;
	private static final Object lock = new Object();
	private static HttpHeaders headers;
	private static final String API_URL = "https://api.openai.com/v1/chat/completions";
	private static List<JSONObject> conversationHistory = new LinkedList<>();

	public static void main(String[] args) {

		// Initialize conversation history with system message
		addSystemMessage("You are ChatGPT, a helpful and sometimes funny assistant.");
		System.out.print("Ask a question: ");
		Scanner scanner = new Scanner(System.in);

		while (true) {
			String message = scanner.nextLine();
			if(message.equalsIgnoreCase("exit") || message.equalsIgnoreCase("quit"))
				break;
			String chatGptReply = sendMessage(message);
			System.out.println("ChatGPT: " + chatGptReply);
		}

		scanner.close();
	}

	private static String sendMessage(String userInput) {
		// Add user's message to the conversation history
		addUserMessage(userInput);

		int retryCount = 0;
		int maxRetries = 3; // Retry up to 3 times
		int delay = 5000; // Wait for 5 seconds before retrying

		try {
			// Create the request body
			JSONObject requestBody = new JSONObject();
			requestBody.put("model", "gpt-3.5-turbo"); // Replace with your custom model ID
			requestBody.put("messages", new JSONArray(conversationHistory));
			HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), getHeaders());
			ResponseEntity<String> response = getRestTemplate().exchange(API_URL, HttpMethod.POST, request, String.class);

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
				retryCount++;
				System.out.println("Rate limit exceeded. Retrying in " + (delay / 1000) + " seconds...");
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

	// Singleton RestTemplate
	public static RestTemplate getRestTemplate() {
		if (restTemplate == null) {
			synchronized (lock) {
				if (restTemplate == null)
					restTemplate = new RestTemplate();
			}
		}
		return restTemplate;
	}

	// Singleton HttpHeaders
	private static HttpHeaders getHeaders() {
		if(headers == null) {
			synchronized (lock) {
				if(headers == null) {
					headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);
					headers.setBearerAuth(API_KEY);
				}
			}
		}
		return headers;
	}

}

