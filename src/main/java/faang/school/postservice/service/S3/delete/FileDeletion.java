package faang.school.postservice.service.S3.delete;

@FunctionalInterface
public interface FileDeletion {

    void deleteFile(String key);

}
