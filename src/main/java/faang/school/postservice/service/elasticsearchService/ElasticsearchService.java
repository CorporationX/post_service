package faang.school.postservice.service.elasticsearchService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ElasticsearchService {
    private final ElasticsearchClient elasticsearchClient;

    public void indexPost(Post post) {
        IndexRequest<Map<String, Object>> request = new IndexRequest.Builder<Map<String, Object>>()
                .index("post")
                .id(String.valueOf(post.getId()))
                .document(convertPostToMap(post))
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

    private Map<String, Object> convertPostToMap(Post post) {
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("id", post.getId());
        postMap.put("content", post.getContent());
        postMap.put("authorId", post.getAuthorId());
        postMap.put("projectId", post.getProjectId());
        postMap.put("published", post.isPublished());
        postMap.put("publishedAt", post.getPublishedAt());
        postMap.put("deleted", post.isDeleted());
        postMap.put("createdAt", post.getCreatedAt());
        postMap.put("updatedAt", post.getUpdatedAt());
        postMap.put("scheduledAt", post.getScheduledAt());
        postMap.put("hashtagIds", post.getHashtags().stream().map(Hashtag::getId).toList());
        if (post.getAd() != null) {
            postMap.put("adId", post.getAd().getId());
        }
        return postMap;
    }
}
