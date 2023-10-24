package ua.zxc.cowbot.postgresql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "queues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"places"})
@ToString(exclude = {"places"})
public class QueueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "size", nullable = false)
    private Integer size;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;

    @OneToMany(mappedBy = "queue", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<PlaceEntity> places = new ArrayList<>();

    public void addPlace(PlaceEntity place) {
        this.places.add(place);
        place.setQueue(this);
    }
}
