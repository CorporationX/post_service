package faang.school.postservice.controller.like;

import faang.school.postservice.dto.post.ReadPostDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//@Controller
//@ResponseBody//отключаем ViewResolvers - обработчки представлений и просим диспачер сервелет(мапер http запросов на контроллеры) вернуть ответ как есть. Так как у нас нет МодельВъюф компонента, мы перестаем хранить состояние
@RestController
@RequestMapping("api/v1/posts") // версия интерфейса
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/{id}")
    public ReadPostDto getPostById(@PathVariable Long id) {
        return postService.findById(id);
    }
}
