package faang.school.postservice.service.elasticsearchService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticsearchService {
    private final ElasticsearchClient elasticsearchClient;

    public void indexPost(Post post) {
        IndexRequest<Post> request = new IndexRequest.Builder<Post>()
                .index("post")
                .id(String.valueOf(post.getId()))
                .document(post)
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

    public List<Post> searchPostsByHashtag(String hashtag) {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("post")
                .query(q -> q
                        .match(m -> m
                                .field("hashtags.name")
                                .query(hashtag)
                        )
                )
                .build();

        SearchResponse<Post> searchResponse;
        try {
            searchResponse = elasticsearchClient.search(searchRequest, Post.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute Elasticsearch search request", e);
        }

        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }
}
