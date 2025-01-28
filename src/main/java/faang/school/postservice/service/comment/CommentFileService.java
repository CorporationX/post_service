package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentFileReadDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.FileMapper;
import faang.school.postservice.model.CommentFile;
import faang.school.postservice.model.File;
import faang.school.postservice.repository.CommentFileRepository;
import faang.school.postservice.repository.FileRepository;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class CommentFileService {
    private static final int MAX_IMAGE_SIZE_MB = 5;
    private static final int MB_TO_BYTES = 1048576;
    private final S3Service s3Service;
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;
    private final CommentFileRepository commentFileRepository;
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    public CommentFileReadDto uploadImage(long commentId, MultipartFile file) {
        String folder = commentId + "_comment_attachment";
        File newFile = s3Service.uploadFile(file, folder);
        newFile = fileRepository.save(newFile);

        CommentFile commentFile = new CommentFile();
        commentFile.setComment(commentService.getCommentById(commentId));
        commentFile.setFile(newFile);
        commentFileRepository.save(commentFile);

        return commentMapper.toFileDto(commentFile);
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

    public void verifyImageSize(File file) {
        if (file.getSize() > MAX_IMAGE_SIZE_MB * MB_TO_BYTES) {
            throw new DataValidationException("Размер файла не должен превышать " + MAX_IMAGE_SIZE_MB + " Мб");
        }
    }

    public File getFileById(long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("Файл с ID " + fileId + " не найден"));
    }
}
