package ua.zxc.cowbot.redis.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash(value = "USER")
@Data
@NoArgsConstructor
public class UserHashEntity implements Serializable {

    private Long chatId;
    private Long userId;
    private String userName;
    private LocalDateTime localDateTime;
}
