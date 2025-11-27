package ssafy.mmt.domain.todo.dto.response;

public record TodoSearchResponse(
        Long todoId,
        String content,
        Boolean isDone
) {
}
