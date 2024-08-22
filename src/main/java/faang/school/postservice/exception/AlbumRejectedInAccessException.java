package faang.school.postservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlbumRejectedInAccessException extends IllegalArgumentException {
    public AlbumRejectedInAccessException(long albumId) {
        super(String.format("Could not get access to Album with ID: %d", albumId));
        log.error(super.getMessage());
    }
}
