package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostDto createDraft(PostDto dto, List<MultipartFile> files);

    PostDto createDraft(PostDto dto);

    PostDto publishPost(Long postId);

    PostDto updatePost(Long postId, PostDto postDto, List<MultipartFile> newFiles);

    PostDto updatePost(Long postId, PostDto postDto);

    void deletePost(Long postId);

    PostDto getPost(Long postId);

    List<PostDto> getAllDraftsByAuthorId(Long userId);

    List<PostDto> getAllDraftsByProjectId(Long projectId);

    List<PostDto> getAllPostsByAuthorId(Long authorId);

    List<PostDto> getAllPostsByProjectId(Long projectId);

    void correctContentDraftPostsByLanguageToolAI();

    Post getExistingPost(Long postId);
}
