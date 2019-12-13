package yay.linda.genericbackend.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yay.linda.genericbackend.api.error.ErrorDTO;

import static yay.linda.genericbackend.api.error.ErrorMessages.UNEXPECTED_ERROR;

@Api(tags = "Default Controller")
@RestController
@RequestMapping("")
@CrossOrigin
public class DefaultController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @ApiOperation(value = "Health check endpoint")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful healthcheck"),
            @ApiResponse(code = 500, message = UNEXPECTED_ERROR, response = ErrorDTO.class)
    })
    @GetMapping("/healthcheck")
    public ResponseEntity<String> healthcheck() {
        return ResponseEntity.ok("Status: UP");
    }

    @ApiOperation(value = "Test WebSocket Endpoint")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully sent message on websocket"),
            @ApiResponse(code = 500, message = UNEXPECTED_ERROR, response = ErrorDTO.class)
    })
    @GetMapping("/websocket")
    public ResponseEntity<String> websocket() {
        this.messagingTemplate.convertAndSend("/topic/test", "test");
        return ResponseEntity.ok("Websocket message emitted");
    }
}
