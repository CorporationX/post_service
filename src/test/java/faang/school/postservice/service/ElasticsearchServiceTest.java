package faang.school.postservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.elasticsearchService.ElasticsearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ElasticsearchServiceTest {
    @InjectMocks
    private ElasticsearchService elasticsearchService;

    @Mock
    private ElasticsearchClient elasticsearchClient;

    private PostDto postDto;
    private Long postId;

    @BeforeEach
    void setUp() {
        postDto = new PostDto();
        postDto.setId(1L);
        postDto.setHashtagNames(Arrays.asList("#1", "#2"));
        postId = 1L;
    }

    @Test
    @DisplayName("Test indexing a post")
    void testIndexPost() throws IOException {
        IndexResponse indexResponse = mock(IndexResponse.class);
        doReturn(indexResponse).when(elasticsearchClient).index(any(IndexRequest.class));

        elasticsearchService.indexPost(postDto);

        verify(elasticsearchClient, times(1)).index(any(IndexRequest.class));
    }

    @Test
    @DisplayName("Test removing a post")
    void testRemovePost() throws IOException {
        DeleteResponse deleteResponse = mock(DeleteResponse.class);
        doReturn(deleteResponse).when(elasticsearchClient).delete(any(DeleteRequest.class));

        elasticsearchService.removePost(postId);

        verify(elasticsearchClient, times(1)).delete(any(DeleteRequest.class));
    }

    @Test
    @DisplayName("Test searching posts by hashtag")
    void testSearchPostsByHashtag() throws IOException {
        Hit<PostDto> hit = Hit.of(h -> h
                .index("post")
                .id("1")
                .source(postDto)
        );

        HitsMetadata<PostDto> hitsMetadata = HitsMetadata.of(hm -> hm
                .total(t -> t
                        .value(1)
                        .relation(TotalHitsRelation.Eq)
                )
                .hits(List.of(hit))
        );

        ShardStatistics shardStatistics = ShardStatistics.of(ss -> ss
                .total(1)
                .successful(1)
                .skipped(0)
                .failed(0)
        );

        SearchResponse<PostDto> searchResponse = SearchResponse.of(sr -> sr
                .took(1)
                .timedOut(false)
                .shards(shardStatistics)
                .hits(hitsMetadata)
        );

        when(elasticsearchClient.search(any(SearchRequest.class), eq(PostDto.class))).thenReturn(searchResponse);

        List<PostDto> result = elasticsearchService.searchPostsByHashtag("1#");

        assertEquals(1, result.size());
        assertEquals(postDto, result.get(0));
        verify(elasticsearchClient, times(1)).search(any(SearchRequest.class), eq(PostDto.class));
    }
}