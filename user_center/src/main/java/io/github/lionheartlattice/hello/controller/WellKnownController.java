package io.github.lionheartlattice.hello.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WellKnownController {
    @RequestMapping("/.well-known/**")
    public ResponseEntity<Void> handleUnknownWellKnown() {
        return ResponseEntity.notFound()
                             .build();
    }
}
