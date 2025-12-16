package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.event.UserBanEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	public ProducerFactory<String, Object> stringObjectProducerFactory() {
		Map<String, Object> configProps = getCommonConfig(StringSerializer.class, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, Object> stringObjectKafkaTemplate() {
		return new KafkaTemplate<>(stringObjectProducerFactory());
	}

	@Bean
	public ProducerFactory<Long, CommentEvent> longCommentEventProducerFactory() {
		Map<String, Object> configProps = getCommonConfig(LongSerializer.class, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<Long, CommentEvent> longCommentEventKafkaTemplate() {
		return new KafkaTemplate<>(longCommentEventProducerFactory());
	}

	@Bean
	public ProducerFactory<String, UserBanEvent> userBanEventProducerFactory() {
		Map<String, Object> configProps = getCommonConfig(StringSerializer.class, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, UserBanEvent> userBanEventKafkaTemplate() {
		return new KafkaTemplate<>(userBanEventProducerFactory());
	}

	@Bean
	public ProducerFactory<String, PostEvent> postEventProducerFactory() {
		Map<String, Object> configProps = getCommonConfig(StringSerializer.class, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, PostEvent> postEventKafkaTemplate() {
		return new KafkaTemplate<>(postEventProducerFactory());
	}

	private Map<String, Object> getCommonConfig(Class<?> keySerializerClass, Class<?> valueSerializerClass) {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);
		configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
		return configProps;
	}
}