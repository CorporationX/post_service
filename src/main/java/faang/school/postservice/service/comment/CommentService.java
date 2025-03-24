package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.event.comment.CommentEventType;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.File;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.comment.CommentCreateMessagePublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.FileRepository;
import faang.school.postservice.service.UserService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private static final long MB_TO_BYTES = 1048576;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;
    private final S3Service s3Service;
    private final FileRepository fileRepository;
    private final CommentCreateMessagePublisher commentCreateMessagePublisher;
    @Value("${services.s3.max_image_size}")
    private int maxImageSize;

    public CommentReadDto create(CommentCreateDto createDto) {
        validateCommentCreation(createDto);
        Comment newComment = commentMapper.toEntity(createDto);
        newComment = commentRepository.save(newComment);
        commentCreateMessagePublisher.publish(
                commentMapper.toEvent(newComment, CommentEventType.CREATE)
        );

        return commentMapper.toDto(newComment);
    }

    public CommentReadDto update(CommentUpdateDto updateDto) {
        Comment comment = getCommentById(updateDto.id());

        validateEditorAndAuthorEquality(updateDto.editorId(), comment.getAuthorId());

        commentMapper.updateEntityFromDto(updateDto, comment);
        commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    public List<CommentReadDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .map(commentMapper::toDto)
                .toList();
    }

    public void remove(long commentId) {
        commentRepository.deleteById(commentId);
    }

    public CommentReadDto uploadImage(long commentId, MultipartFile file) {
        Comment comment = getCommentById(commentId);
        validateImageUpload(file);

        String folder = commentId + "_comment_attachments";
        File newFile = s3Service.uploadFile(file, folder);
        newFile = fileRepository.save(newFile);

        comment.getFiles().add(newFile);

        comment = commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public InputStream downloadImage(long imageId) {
        File file = getFileById(imageId);
        return s3Service.downloadFile(file.getKey());
    }

    public void removeImage(long imageId) {
        File file = getFileById(imageId);
        s3Service.deleteFile(file.getKey());
        fileRepository.deleteById(imageId);
    }

    public Comment getCommentById(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментария с ID " + commentId + " не найден"));
    }

    private void validateEditorAndAuthorEquality(long editorId, long authorId) {
        if (editorId != authorId) {
            throw new BusinessException("Редактировать комментарий может только его автор");
        }
    }

    private void validateCommentCreation(CommentCreateDto createDto) {
        userService.getUserDtoById(createDto.authorId());
        Post post = postService.getPostById(createDto.postId());
        if (!post.isPublished()) {
            throw new BusinessException("Нельзя оставлять комментарий на не опубликованный пост");
        }
        if (post.isDeleted()) {
            throw new BusinessException("Нельзя оставлять комментарий на удаленный пост");
        }
    }

    public void validateImageUpload(MultipartFile file) {
        if (file.getSize() > maxImageSize * MB_TO_BYTES) {
            throw new DataValidationException("Размер файла не должен превышать " + maxImageSize + " Мб");
        }
        String fileType = file.getContentType();
        if (fileType == null || !fileType.startsWith("image/")) {
            throw new DataValidationException("Неверный тип файла. Разрешено загружать только изображения");
        }
    }

    public File getFileById(long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("Файл с ID " + fileId + " не найден"));
    }

}
