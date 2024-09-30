package fa.training.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<T> {
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private List<T> data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageDto<?> pageDto)) return false;
        List data = ((PageDto)o).data;
        for (int i = 0; i < data.size(); i++) {
            if (!this.data.get(i).equals(data.get(i))) {
                return false;
            }
        }
        return page == pageDto.page && size == pageDto.size && totalPages == pageDto.totalPages && totalElements == pageDto.totalElements ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, size, totalPages, totalElements, data);
    }
}
