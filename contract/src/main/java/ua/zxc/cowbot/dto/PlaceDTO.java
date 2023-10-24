package ua.zxc.cowbot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class PlaceDTO {

    @NotNull(message = "The user id cannot be null")
    private Long userId;

    @NotNull(message = "The chat id cannot be null")
    private Long queueId;

    @NotNull(message = "The number of place cannot be null")
    private Integer number;

    private UserDTO user;

    private QueueDTO queue;
}
