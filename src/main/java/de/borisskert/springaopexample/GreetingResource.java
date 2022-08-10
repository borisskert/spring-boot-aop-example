package de.borisskert.springaopexample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/greeting")
public class GreetingResource {

    @GetMapping
    @Profiled("get-greeting")
    public String get() {
        return "Hello World!";
    }

    @GetMapping("/error")
    @Profiled("get-greeting-error")
    public String throwsError() {
        throw new RuntimeException("Throwing error for test");
    }
}
