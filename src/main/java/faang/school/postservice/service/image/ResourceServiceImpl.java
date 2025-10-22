package faang.school.postservice.service.image;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceService resourceService;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public List<Resource> uploadImages(Long postId, List<MultipartFile> files); {


    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> getResourcesByPostId(long postId); {
        Post post= postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(...));

        if (post.isDeleted) {
            log.info("Post with id {} has been deleted", post.getId());
        } else {
            log.info("Post with id {} is not deleted", post.getId());
        }

        if (post.getResources().size() <= 10) {

        }


    @Override
    @Transactional
    public List<Resource> deleteResource(long postId, long resourceId); {

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadResource(Long postId, Long resourceId); {

    }
}
