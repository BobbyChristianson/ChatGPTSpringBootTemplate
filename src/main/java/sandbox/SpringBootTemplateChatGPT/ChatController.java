package sandbox.SpringBootTemplateChatGPT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/gpt")
public class ChatController {

    @Autowired
    private ChatService service;

    // You will want to change this to a @PostMapping. I've left this here, so you can just run this right out of the box
    @GetMapping("/chat")
    public String getClosestParkingSpot(@RequestParam(value = "message", required = false, defaultValue = "How fast can a rabbit run") String message){
        return service.chat(message);
    }
}
