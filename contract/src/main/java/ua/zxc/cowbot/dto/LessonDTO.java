package ua.zxc.cowbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Schema(name = "Lesson")
public class LessonDTO {

    @Schema(name = "id", example = "1")
    @JsonProperty("id")
    private Long id;

    @Schema(name = "chat_id", example = "123456", required = true)
    @NotNull(message = "The chat id must not be null")
    @JsonProperty("chatId")
    Long chatId;

    @Schema(name = "name", example = "Lesson name", required = true)
    @NotNull(message = "The name of lesson must not be null")
    @JsonProperty("name")
    String name;

    @Schema(name = "full_teacher_name", example = "Full teacher name", required = true)
    @NotNull(message = "The teacher name must not be null")
    @JsonProperty("teacher")
    String fullTeacherName;

    @Schema(name = "url", example = "url.com", required = true)
    @NotNull(message = "The URL must not be null")
    @URL(message = "This is not a URL")
    @JsonProperty("url")
    String url;

    @Schema(name = "email", example = "email@gmail.com")
    @Email(message = "This is not an email address")
    @JsonProperty("email")
    String email;

    @Schema(name = "phone", example = "+380999999999")
    @JsonProperty("phone")
    String phone;
}
