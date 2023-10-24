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
public class RespectDTO {

    @NotNull(message = "The user id cannot be null")
    private Long userId;

    @NotNull(message = "The chat id cannot be null")
    private Long chatId;

    private int numberThisMonth;

    private int numberPrevMonth;

    private int totalNumber;

    private UserDTO user;

    private ChatDTO chat;
}
