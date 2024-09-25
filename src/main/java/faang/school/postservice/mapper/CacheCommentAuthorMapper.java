package faang.school.postservice.mapper;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.CacheCommentAuthor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheCommentAuthorMapper {
    @Value("${spring.author-comment.ttl}")
    private int authorTtl;

    public List<CacheCommentAuthor> toCacheCommentAuthor(List<UserDto> userDtos) {
        return userDtos.stream().map(userDto -> CacheCommentAuthor.builder()
                .id(userDto.getId())
                .userName(userDto.getUsername())
                .ttl(authorTtl)
                .build()).toList();
    }
}
