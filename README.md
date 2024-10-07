# SpringBootTemplateChatGPT

Spring Boot backend application template for ChatGPT. 
This application is pretty much a Spring Boot skeleton of the ChatGPT browser.
There is a small setup but it can pretty much be run right out of the box.

## How to Setup
- Make sure you have Maven, git and an IDE like Intellij you can run Spring Boot in
- Make sure you have an OpenAI API key ready. If you don't refer to [https://platform.openai.com/docs/api-reference/authentication](https://platform.openai.com/docs/api-reference/authentication)
- Add your API key to your environment variables by adding `export OPENAI_KEY=your_openai_key` to you .bash_profile or .bashrd
- Add your custom GPT by adding your model id here in  ChatService.java
- ![](../../../Desktop/Screenshot 2024-10-07 at 2.23.30 PM.png)
- Give [http://localhost:8080/api/gpt/chat]() a go in your browser to start the fun. You will want to change your controller @GetMapping to a @PostMapping but I've left it as a GET just so it can be run right out of da box

