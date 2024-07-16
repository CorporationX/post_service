package faang.school.postservice.controller;

import io.swagger.annotations.Api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
@Api(description = "Пример контроллера для демонстрации Swagger")
public class Controller {
//
//    @GetMapping
//    @ApiOperation("Получить список UserDto")
//    public List<UserDto> getAll() {
//        return List.of(new UserDto(1L, "test", "email@gmail.com"));
//    }
//
//    @PostMapping
//    public void post(@RequestBody UserDto userDto) {
//
//    }
//
//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable long id) {
//
//    }
}
