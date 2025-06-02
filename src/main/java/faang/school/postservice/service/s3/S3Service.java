package faang.school.postservice.service.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final A s3Client;
}
