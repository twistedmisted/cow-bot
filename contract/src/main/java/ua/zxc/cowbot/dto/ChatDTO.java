package ua.zxc.cowbot.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Schema(name = "Chat")
public class ChatDTO {

    @Schema(name = "id", example = "123456", required = true)
    @NotNull(message = "The id of chat can not be null")
    private Long id;

    @Schema(name = "name", example = "Chat name", required = true)
    @NotNull(message = "The name of chat can not be null")
    private String name;

    @Schema(name = "group_name", example = "XX-00")
    @JsonIgnore
    private String groupName;
}
