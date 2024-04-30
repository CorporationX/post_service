package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostViewDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PostViewMapperTest {

    private final PostViewMapper postViewMapper = Mappers.getMapper(PostViewMapper.class);

    @Test
    public void testToDtoWhenEntityProvidedThenDtoReturnedWithCorrectPostId() {
        Post post = new Post();
        post.setId(1L);
        PostView postView = new PostView();
        postView.setPost(post);

        PostViewDto postViewDto = postViewMapper.toDto(postView);

        assertThat(postViewDto.getPostId()).isEqualTo(post.getId());
    }

    @Test
    public void testToDtoWhenNullEntityProvidedThenNullDtoReturned() {
        PostViewDto postViewDto = postViewMapper.toDto(null);

        assertThat(postViewDto).isNull();
    }

    @Test
    public void testToEntityWhenValidDtoProvidedThenEntityReturnedWithNullPost() {
        PostViewDto postViewDto = new PostViewDto();
        postViewDto.setId(1L);
        postViewDto.setViewerId(2L);
        postViewDto.setPostId(3L);

        PostView postView = postViewMapper.toEntity(postViewDto);

        assertThat(postView.getPost()).isNull();
        assertThat(postView.getId()).isEqualTo(postViewDto.getId());
        assertThat(postView.getViewerId()).isEqualTo(postViewDto.getViewerId());
    }

    @Test
    public void testToEntityWhenNullDtoProvidedThenNullReturned() {
        PostView postView = postViewMapper.toEntity(null);

        assertThat(postView).isNull();
    }
}