package faang.school.postservice.swagger_example;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="ExampleController", description = "The Example Api")
@RestController
@RequestMapping
public class ExampleController {
    @GetMapping("/getById/{id}")
    @Operation(description = "Some descripton of this method")
    public ExampleDTO testMethod(@PathVariable Long id, @RequestParam(required = false) Long param, ExampleDTO exampleDTO) {
        exampleDTO.setId(id + param);
        return exampleDTO;
    }

    @PostMapping("/create")
    @Operation(description = "Method for create something")
    public ExampleDTO createMethod(@RequestBody ExampleDTO exampleDTO) {
        exampleDTO.setId(System.currentTimeMillis());
        exampleDTO.setName("String after return");
        return exampleDTO;
    }

}
