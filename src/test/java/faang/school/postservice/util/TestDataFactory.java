package faang.school.postservice.util;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.model.AuthorCache;
import faang.school.postservice.redis.model.PostCache;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Long.MAX_VALUE;
import static java.util.List.of;

@UtilityClass
public final class TestDataFactory {

    public static final Long ID = 1L;
    public static final Long POST_AUTHOR_ID = 1L;

    public static final Long INVALID_ID = MAX_VALUE;

    public static List<UserDto> getUserDtoList() {
        var userAlex = UserDto.builder()
                .id(1L)
                .username("Alex")
                .email("alex@gmail.com")
                .build();

        var userAnna = UserDto.builder()
                .id(2L)
                .username("Anna")
                .email("anna@gmail.com")
                .build();

        var userOlga = UserDto.builder()
                .id(3L)
                .username("Olga")
                .email("olga@gmail.com")
                .build();

        return of(userOlga, userAnna, userAlex);
    }


    public static PostDto createPostDto() {
        var comment = createComment();
        return PostDto.builder()
                .id(123L)
                .content("Content")
                .authorId(12L)
                .likes(8)
                .views(100)
                .comments(of(comment))

                .build();
    }

    public static PostCache createPostCache(){
        var comment = createComment();
        var comments = new CopyOnWriteArrayList<CommentDto>();
        comments.add(comment);
        return PostCache.builder()
                .id(123L)
                .content("Content")
                .authorId(12L)
                .likes(8)
                .views(100)
                .comments(comments)
                .build();
    }

    public static CommentDto createComment(){
        return CommentDto.builder()
                .id(1L)
                .content("Comment1")
                .build();
    }

    public static List<UserDto> createUserDtoList() {
        var userDto = createUserDto();
        return of(userDto);
    }

    public static UserDto createUserDto(){
        return UserDto
                .builder()
                .id(12345L)
                .username("testUserName")
                .email("test@email.com")
                .build();
    }

    public static AuthorCache createAuthorCache() {
        return new AuthorCache(12345L, "testUserName", "test@email.com");
    }
}
