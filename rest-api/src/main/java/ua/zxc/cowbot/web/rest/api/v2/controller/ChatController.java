package ua.zxc.cowbot.web.rest.api.v2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.service.ChatService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Chat", description = "The Chat REST API")
@RestController("ChatController2")
@RequestMapping("/api/v2/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(
            summary = "Get user's chats",
            description = "Get the list of chats by user id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(schemaProperties = @SchemaProperty(
                                    name = "chats",
                                    array = @ArraySchema(schema = @Schema(implementation = ChatDTO.class))
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    )
            }
    )
    @GetMapping(value = "/user/{userId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getAllByUserId(@Parameter(description = "User id")
                                                              @PathVariable Long userId) {
        Map<String, Object> responseBody = new HashMap<>();
        List<ChatDTO> chatsByUserId = chatService.getAllByUsersId(userId);
        if (chatsByUserId.isEmpty()) {
            responseBody.put("message", "Не вдається знайти зареєстрованих чатів для Вас.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
        responseBody.put("chats", chatsByUserId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
