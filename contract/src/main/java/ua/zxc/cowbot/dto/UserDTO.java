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
public class UserDTO {

    @NotNull(message = "The user id can not be null")
    private Long id;

    @NotNull(message = "The username can not be null")
    private String username;

    @NotNull(message = "The first name can not be null")
    private String firstName;
}
