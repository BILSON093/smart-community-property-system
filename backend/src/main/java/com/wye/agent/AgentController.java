package com.wye.agent;

import com.wye.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/agent")
public class AgentController {

    @Autowired
    private PropertyAgent propertyAgent;

    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(@RequestBody Map<String, Object> request,
                                            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        String message = (String) request.get("message");

        List<Map<String, Object>> history = null;
        if (request.containsKey("history") && request.get("history") != null) {
            history = (List<Map<String, Object>>) request.get("history");
        }

        if (message == null || message.trim().isEmpty()) {
            return Result.error("消息不能为空");
        }

        Map<String, Object> result = propertyAgent.chat(userId, message, history);
        return Result.success(result);
    }
}
