package faang.school.postservice.controller;

import faang.school.postservice.dto.TestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("test")
public class TestController {

    @PostMapping("/create")
    @Operation(
            summary = "Create a new TestDto",
            parameters = @Parameter(
                    name = "x-user-id",
                    in = ParameterIn.HEADER, required = true,
                    description = "User ID"
            )
    )
    public TestDto create(@RequestBody TestDto testDto) {
        return TestDto.builder()
                .name(testDto.getName())
                .age(testDto.getAge())
                .build();
    }
}
