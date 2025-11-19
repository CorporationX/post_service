package faang.school.postservice.controller;

import faang.school.postservice.test_dto.TestDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/test")
public class TestController {
    @PostMapping("/validation")
    public void validate(@Valid @RequestBody TestDto dto) {}

    @GetMapping("/not_found")
    public void throwNotFound() {
        throw new NoSuchElementException("Post will not be found");
    }

    @GetMapping("/illegal/argument")
    public void throwIllegalArgument() {
        throw new IllegalArgumentException("Bad input value");
    }

    @GetMapping("/runtime/exception")
    public void throwRuntimeException() {
        throw new RuntimeException("Unexpected Feign client error");
    }

    @GetMapping("/null")
    public void throwNull() {
        throw new NullPointerException("ID of the author of the post should not be null");
    }
}
