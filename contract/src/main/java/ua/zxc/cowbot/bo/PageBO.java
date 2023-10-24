package ua.zxc.cowbot.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class PageBO<T> {

    private List<T> content = new ArrayList<>();

    private int currentPageNumber;

    private int totalPages;

    public boolean isEmpty() {
        return this.content.isEmpty();
    }
}
