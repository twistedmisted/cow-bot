package ua.zxc.cowbot.postgresql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ua.zxc.cowbot.postgresql.entity.embeddedid.UserChatId;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import static javax.persistence.CascadeType.MERGE;

@Entity
@Table(name = "respects")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class RespectEntity {

    @EmbeddedId
    private UserChatId id = new UserChatId();

    @Column(name = "number_this_month")
    private Integer numberThisMonth;

    @Column(name = "number_prev_month")
    private Integer numberPrevMonth;

    @Column(name = "total_number")
    private Integer totalNumber;

    @MapsId("userId")
    @ManyToOne(optional = false, cascade = MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @MapsId("chatId")
    @ManyToOne(optional = false, cascade = MERGE)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;
}
