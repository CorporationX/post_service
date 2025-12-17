package faang.school.postservice.mapper.post;

import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ad.Ad;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RedisPostMapperTest {

    private final RedisPostMapper redisPostMapper =
            Mappers.getMapper(RedisPostMapper.class);

    @Test
    public void toRedisPostDto_successfullyMappes() {
        long anyLong = 11L;
        long anyOtherLong = 555L;
        String anyString = "anyString";

        Like anyLike = new Like();
        anyLike.setId(anyLong);
        Comment anyComment = new Comment();
        anyComment.setId(anyOtherLong);
        Album anyAlbum = new Album();
        anyAlbum.setId(anyOtherLong);
        Ad anyAd = new Ad();
        anyAd.setId(anyLong);
        Resource anyResource = new Resource();
        anyResource.setId(anyOtherLong);

        Post anyPost = new Post();
        anyPost.setId(anyLong);
        anyPost.setContent(anyString);
        anyPost.setAuthorId(anyLong);
        anyPost.setProjectId(anyOtherLong);
        anyPost.setLikes(List.of(anyLike));
        anyPost.setComments(List.of(anyComment));
        anyPost.setAlbums(List.of(anyAlbum));
        anyPost.setAd(anyAd);
        anyPost.setResources(List.of(anyResource));

        assertEquals(anyLong, redisPostMapper.toRedisPostDto(anyPost).getId());
        assertEquals(anyString, redisPostMapper.toRedisPostDto(anyPost).getContent());
        assertEquals(anyLong, redisPostMapper.toRedisPostDto(anyPost).getAuthorId());
        assertEquals(anyOtherLong, redisPostMapper.toRedisPostDto(anyPost).getProjectId());
        assertEquals(List.of(anyLong), redisPostMapper.toRedisPostDto(anyPost).getLikeIds());
        assertEquals(List.of(anyOtherLong), redisPostMapper.toRedisPostDto(anyPost).getCommentIds());
        assertEquals(List.of(anyOtherLong), redisPostMapper.toRedisPostDto(anyPost).getAlbumIds());
        assertEquals(anyLong, redisPostMapper.toRedisPostDto(anyPost).getAdId());
        assertEquals(List.of(anyOtherLong), redisPostMapper.toRedisPostDto(anyPost).getResourceIds());
    }
}
