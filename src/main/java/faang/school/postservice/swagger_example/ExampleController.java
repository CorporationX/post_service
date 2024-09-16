package faang.school.postservice.swagger_example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ExampleController {
    public ExampleDTO testMethod(ExampleDTO exampleDTO) {
        exampleDTO.setId(exampleDTO.getId() + 100L);
        exampleDTO.setName(exampleDTO.getName() + "someString");
        return exampleDTO;
    }


}
