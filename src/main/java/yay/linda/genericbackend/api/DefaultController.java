package yay.linda.genericbackend.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthcheck")
@CrossOrigin
public class DefaultController {
    @GetMapping("")
    public ResponseEntity<String> healthcheck() {
        return ResponseEntity.ok("Status: UP");
    }
}
