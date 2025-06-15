package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeDataFetcher {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;

    @Transactional(readOnly = true)
    public Page<LikeDto> getLikesPageByPostId(Long postId, Pageable pageable) {
        if (postId == null) {
            log.error("getLikesPageByPostId called with null postId. Returning empty list.");
            throw new IllegalArgumentException("postId cannot be null.");
        }
        if (!likeRepository.existsLikesByPostId(postId)) {
            log.warn("getLikesPageByPostId called with non-existing postId {}. Returning empty list.", postId);
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        Page<Like> page = likeRepository.getLikesByPostId(postId, pageable);
        List<LikeDto> dtos = page.stream()
                .map(likeMapper::toDto)
                .toList();
        return new PageImpl<>(
                dtos,
                pageable,
                page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<LikeDto> getLikesPageByCommentId(Long commentId, Pageable pageable) {
        if (commentId == null) {
            log.error("getLikesPageByCommentId called with null commentId. Returning empty list.");
            throw new IllegalArgumentException("commentId cannot be null.");
        }
        if (!likeRepository.existsLikesByCommentId(commentId)) {
            log.warn("getLikesPageByCommentId called with non-existing commentId {}. Returning empty list.",
                    commentId);
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        Page<Like> page = likeRepository.getLikesByCommentId(commentId, pageable);
        List<LikeDto> dtos = page.stream()
                .map(likeMapper::toDto)
                .toList();
        return new PageImpl<>(
                dtos,
                pageable,
                page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public long getLikesCountByPostId(Long postId) {
        if (postId == null) {
            log.error("getLikesCountByPostId called with null postId. Returning 0.");
            throw new IllegalArgumentException("postId cannot be null.");
        }
        return likeRepository
                .getLikesByPostId(postId, Pageable.unpaged())
                .getTotalElements();
    }

    @Transactional(readOnly = true)
    public long getLikesCountByCommentId(Long commentId) {
        if (commentId == null) {
            log.error("getLikesCountByCommentId called with null commentId. Returning 0.");
            throw new IllegalArgumentException("commentId cannot be null.");
        }
        return likeRepository
                .getLikesByCommentId(commentId, Pageable.unpaged())
                .getTotalElements();
    }
}
