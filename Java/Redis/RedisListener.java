import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisListener implements MessageListener {
  @Override
  public void onMessage(Message message, byte[] pattern) {
    System.out.println(new String(message.getBody()));
  }
}
