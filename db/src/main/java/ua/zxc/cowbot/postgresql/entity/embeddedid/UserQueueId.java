package ua.zxc.cowbot.postgresql.entity.embeddedid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class UserQueueId implements Serializable {

    private static final long serialVersionUID = 7783505010559718403L;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "queue_id", nullable = false)
    private Long queueId;
}
