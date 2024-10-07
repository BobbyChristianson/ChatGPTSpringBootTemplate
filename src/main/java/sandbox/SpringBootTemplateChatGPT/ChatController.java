package sandbox.SpringBootTemplateChatGPT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/gpt")
public class ChatController {

    @Autowired
    private ChatService service;

    @PostMapping("/chat")
    public String chatWithGPT(@RequestParam(value = "message", required = true) String message){
        return service.chat(message);
    }
}
