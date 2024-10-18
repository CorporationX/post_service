package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.repository.AdRepository;
import faang.school.postservice.util.container.PostContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostDataPreparerTest {
    @InjectMocks
    private PostDataPreparer postDataPreparer;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private AdRepository adRepository;

    private PostMapper mapper = new PostMapperImpl();
    private PostContainer container = new PostContainer();

    @Test
    void testPrepareForCreate() {
        // given
        PostDto dto = container.dto();
        Post postEntity = mapper.toEntity(dto);
        Post entityExp = container.entity();
        when(likeRepository.findAllById(dto.getLikeIds())).thenReturn(entityExp.getLikes());
        when(commentRepository.findAllById(dto.getCommentIds())).thenReturn(entityExp.getComments());
        when(albumRepository.findAllById(dto.getAlbumIds())).thenReturn(entityExp.getAlbums());
        when(resourceRepository.findAllById(dto.getResourceIds())).thenReturn(entityExp.getResources());
        when(adRepository.findById(dto.getAdId())).thenReturn(Optional.of(entityExp.getAd()));

        // when
        Post entityActual = postDataPreparer.prepareForCreate(dto, postEntity);

        // then
        entityExp.setCreatedAt(entityActual.getCreatedAt());
        entityExp.setUpdatedAt(entityActual.getUpdatedAt());
        assertEquals(entityExp, entityActual);
    }

    @Test
    void testPrepareForUpdate() {
        PostDto dto = PostDto.builder()
                .id(container.postId())
                .content(container.content() + " update")
                .adId(container.adId() + 1)
                .build();

        Post entity = Post.builder()
                .id(container.postId())
                .content(container.content())
                .ad(container.ad())
                .updatedAt(container.updatedAt())
                .build();

        Ad newAd = Ad.builder()
                .id(dto.getAdId())
                .build();
        when(adRepository.findById(dto.getAdId())).thenReturn(Optional.of(newAd));

        Post postExp = Post.builder()
                .id(container.postId())
                .content(dto.getContent())
                .ad(newAd)
                .build();

        // when
        Post postActual = postDataPreparer.prepareForUpdate(dto, entity);

        // then
        postExp.setUpdatedAt(postActual.getUpdatedAt());
        assertEquals(postExp, postActual);
    }

    @Test
    void testPrepareForPublish() {
        boolean isNotPublished = container.published();
        // given
        Post postEntity = Post.builder()
                .id(container.postId())
                .published(isNotPublished)
                .build();

        Post postExp = Post.builder()
                .id(container.postId())
                .published(!isNotPublished)
                .build();

        // when
        Post postActual = postDataPreparer.prepareForPublish(postEntity);

        // then
        postExp.setPublishedAt(postActual.getPublishedAt());
        assertEquals(postExp, postActual);
    }
}