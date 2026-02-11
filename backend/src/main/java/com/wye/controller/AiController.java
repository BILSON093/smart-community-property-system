package com.wye.controller;

import com.wye.common.Result;
import com.wye.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * AI客服对话
     */
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String question = request.get("question");
        String imageUrl = request.get("imageUrl");
        Long userId = (Long) httpRequest.getAttribute("userId");

        String reply = aiService.generateReply(userId, question, imageUrl);
        return Result.success(reply);
    }
}
