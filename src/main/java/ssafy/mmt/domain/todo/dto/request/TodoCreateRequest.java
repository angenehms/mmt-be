package ssafy.mmt.domain.todo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TodoCreateRequest(
        @NotNull @Size(min = 1, max = 30) String content
) {
}
