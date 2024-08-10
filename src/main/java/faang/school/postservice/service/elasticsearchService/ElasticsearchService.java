package faang.school.postservice.service.elasticsearchService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticsearchService {
    private final ElasticsearchClient elasticsearchClient;

    public void indexPost(PostDto postDto) {
        IndexRequest<PostDto> request = new IndexRequest.Builder<PostDto>()
                .index("post")
                .id(String.valueOf(postDto.getId()))
                .document(postDto)
                .build();
        try {
            elasticsearchClient.index(request);
        } catch (IOException | ElasticsearchException e) {
            throw new RuntimeException("Failed to index post in Elasticsearch", e);
        }
    }

    public void removePost(Long postId) {
        DeleteRequest request = new DeleteRequest.Builder()
                .index("post")
                .id(String.valueOf(postId))
                .build();
        try {
            elasticsearchClient.delete(request);
        } catch (IOException | ElasticsearchException e) {
            throw new RuntimeException("Failed to delete post from Elasticsearch", e);
        }
    }

    public List<PostDto> searchPostsByHashtag(String hashtag) {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("post")
                .query(q -> q
                        .match(m -> m
                                .field("hashtagNames")
                                .query(hashtag)
                        )
                )
                .build();

        SearchResponse<PostDto> searchResponse;
        try {
            searchResponse = elasticsearchClient.search(searchRequest, PostDto.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute Elasticsearch search request", e);
        }

        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }
}
