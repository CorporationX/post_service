package faang.school.postservice.config.redis.comment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class CommentConfig {

  @Value("${spring.data.redis.channels.comment.name}")
  private String commentChannel;

  @Bean
  public ChannelTopic commentTopic() {
    return new ChannelTopic(commentChannel);
  }

}
