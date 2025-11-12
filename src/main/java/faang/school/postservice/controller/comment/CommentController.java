package faang.school.postservice.controller.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentRedisDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, CommentRedisDto> redisCommentTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @PostMapping
    public CommentDto create(@RequestBody @Valid CommentDto commentDto) {
        return commentService.create(commentDto);
    }

    @PutMapping("/{id}")
    public CommentDto update(@PathVariable long id,
                             @RequestBody @Valid CommentDto commentDto) {
        return commentService.update(id, commentDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        commentService.delete(id);
    }

    @GetMapping("/posts/{id}")
    public List<CommentDto> findAllByPostId(@PathVariable long id) {
        return commentService.findAllByPostId(id);
    }

    @GetMapping("/getComment/{authorId}/{commentId}")
    public void getComment(@PathVariable long authorId, @PathVariable long commentId) {
        String key = "authors_" + authorId;
        Boolean hasKey = stringRedisTemplate.hasKey(key);
        System.out.println("hasKey = " + hasKey);
        System.out.println("redisTemplate.type(key) = " + stringRedisTemplate.type(key));
        String range = stringRedisTemplate.opsForValue().get(key);
        JavaType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, CommentDto.class);
        try {
            Object o = objectMapper.readValue(range, collectionType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
//        System.out.println("redisTemplate.opsForList().range(key, 0, -1) = " + range.get(0)
//                .stream()
//                .filter(comment -> comment.getAuthorId() == authorId)
//                .toList()
//        );

//        redisTemplate.delete(key);

    }
}
