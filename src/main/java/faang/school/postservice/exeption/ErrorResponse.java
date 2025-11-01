package faang.school.postservice.exeption;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorResponse(@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                            LocalDateTime timeStamp,
                            String url,
                            String error,
                            String message,
                            int status
) {
}
