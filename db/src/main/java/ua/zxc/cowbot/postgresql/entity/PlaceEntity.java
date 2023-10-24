package ua.zxc.cowbot.postgresql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ua.zxc.cowbot.postgresql.entity.embeddedid.UserQueueId;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "places")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class PlaceEntity {

    @EmbeddedId
    private UserQueueId id = new UserQueueId();

    @Column(name = "number", nullable = false)
    private Integer number;

    @MapsId("userId")
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @MapsId("queueId")
    @ManyToOne(optional = false)
    @JoinColumn(name = "queue_id", nullable = false)
    private QueueEntity queue;
}
