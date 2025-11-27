package ssafy.mmt.domain.todo.dto.request;

import jakarta.validation.constraints.NotNull;

public record TodoUpdateRequest(
        @NotNull String content,
        @NotNull Boolean isDone
) {
}
