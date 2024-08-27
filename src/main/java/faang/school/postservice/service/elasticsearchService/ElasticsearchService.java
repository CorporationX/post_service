package faang.school.postservice.service.elasticsearchService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.ElasticsearchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchService {
    private final ElasticsearchClient elasticsearchClient;

    @Value("${spring.data.elasticsearch.index-post.index}")
    private String indexPost;

    @Value("${spring.data.elasticsearch.search-by-hashtag.field}")
    private String fieldForMatchByHashtag;

    @Value("${spring.data.elasticsearch.field-for-range.field}")
    private String fieldForRangeById;

    public void indexPost(PostDto postDto) {
        IndexRequest<PostDto> request = new IndexRequest.Builder<PostDto>()
                .index(indexPost)
                .id(String.valueOf(postDto.getId()))
                .document(postDto)
                .build();
        try {
            elasticsearchClient.index(request);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ElasticsearchException(String.format(
                    "Failed to index post id: %d in Elasticsearch", postDto.getId()));
        }
    }

    public void removePost(Long postId) {
        DeleteRequest request = new DeleteRequest.Builder()
                .index(indexPost)
                .id(String.valueOf(postId))
                .build();
        try {
            elasticsearchClient.delete(request);
        } catch (IOException | ElasticsearchException e) {
            log.error(e.getMessage());
            throw new ElasticsearchException(String.format(
                    "Failed to index post id: %d in Elasticsearch", postId));
        }
    }

    public List<PostDto> searchPostsByHashtag(String hashtag, int from, int size) {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(indexPost)
                .query(q -> q
                        .match(m -> m
                                .field(fieldForMatchByHashtag)
                                .query(hashtag)
                        )
                )
                .from(from)
                .size(size)
                .build();

        SearchResponse<PostDto> searchResponse;
        try {
            searchResponse = elasticsearchClient.search(searchRequest, PostDto.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ElasticsearchException("Failed to execute Elasticsearch" +
                    " search request by hashtag name: " + hashtag);
        }

        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }

    public List<PostDto> searchPostsByHashtagAndByMostId(String hashtag, long id) {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(indexPost)
                .query(q -> q
                        .bool(b -> b
                                .must(must -> must
                                        .match(m -> m
                                                .field(fieldForMatchByHashtag)
                                                .query(hashtag)
                                        )
                                )
                                .filter(f -> f
                                        .range(r -> r
                                                .field(fieldForRangeById)
                                                .gt(JsonData.of(id))
                                        )
                                )
                        )
                )
                .build();

        SearchResponse<PostDto> searchResponse;
        try {
            searchResponse = elasticsearchClient.search(searchRequest, PostDto.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ElasticsearchException("Failed to execute Elasticsearch" +
                    " search request by hashtag name: " + hashtag);
        }

        return searchResponse.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();
    }
}
