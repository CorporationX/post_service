package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public PostDto createPost(PostDto postDto) {
        return
    }

    @Transactional
    public PostDto publishPost(long id) {
        return
    }




    public Post getPost(Long postId){
        return  postRepository.findById(postId).orElseThrow(NoSuchElementException::new);
    }

}