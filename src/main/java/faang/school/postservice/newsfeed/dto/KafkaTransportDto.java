package faang.school.postservice.newsfeed.dto;

public record KafkaTransportDto(
        String uniqEventIdentifier,
        String payload
) {
}

