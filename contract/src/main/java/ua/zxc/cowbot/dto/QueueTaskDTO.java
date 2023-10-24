package ua.zxc.cowbot.dto;

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
public class QueueTaskDTO {

    private Long id;

    @NotNull(message = "The user id cannot be null")
    private Long userId;

    @NotNull(message = "The chat id cannot be null")
    private Long chatId;

    @NotNull(message = "The name cannot be null")
    private String name;

    @NotNull(message = "The number try cannot be null")
    private Integer numberTry;

    public boolean isAttemptsOver() {
        return numberTry == 0;
    }
}
