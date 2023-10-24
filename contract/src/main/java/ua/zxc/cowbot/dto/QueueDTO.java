package ua.zxc.cowbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class QueueDTO {

    private Long id;

    @NotNull(message = "The chat id of queue cannot be null")
    private Long chatId;

    @NotNull(message = "The queue name cannot be null")
    private String name;

    @NotNull(message = "The queue size cannot be null")
    @Min(value = 5, message = "The queue size cannot be less than 5")
    private Integer size;

    private List<PlaceDTO> places = new ArrayList<>();
}
