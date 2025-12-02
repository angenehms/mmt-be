package ssafy.mmt.domain.todo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.mmt.common.auth.CustomMemberPrincipal;
import ssafy.mmt.domain.todo.dto.request.TodoCreateRequest;
import ssafy.mmt.domain.todo.dto.request.TodoUpdateRequest;
import ssafy.mmt.domain.todo.dto.response.TodoSearchResponse;
import ssafy.mmt.domain.todo.entity.Todo;
import ssafy.mmt.domain.todo.repository.TodoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public void addTodo (
            TodoCreateRequest tcr,
            CustomMemberPrincipal customMemberPrincipal)
    {
        Long memberId = customMemberPrincipal.getMemberId();
        Todo newTodo = Todo.builder()
                .memberId(memberId)
                .isDone(false)
                .content(tcr.content())
                .build();
        todoRepository.save(newTodo);
    }

    @Transactional
    public List<TodoSearchResponse> getTodo(
            CustomMemberPrincipal customMemberPrincipal
    ) {
        Long memberId = customMemberPrincipal.getMemberId();

        List<Todo> rawList = todoRepository.findByMemberId((memberId));
        List<TodoSearchResponse> formattedList = new ArrayList<>();

        for(Todo curr : rawList) {
            formattedList.add(
                    new TodoSearchResponse(
                            curr.getTodoId(),
                            curr.getContent(),
                            curr.getIsDone()
                    )
            );
        }

        return formattedList;
    }

    @Transactional
    public void deleteTodo(
            CustomMemberPrincipal customMemberPrincipal,
            Long todoId
    ) {
        Long memberId = customMemberPrincipal.getMemberId();

        // 1. ⭐️ SELECT: To-do 항목을 찾고, 없으면 예외 발생
        // 이 과정에서 DB에 SELECT 쿼리가 실행됩니다.
        Todo todo = todoRepository.findByMemberIdAndTodoId(memberId, todoId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID(" + todoId + ")의 To-do 항목을 찾을 수 없습니다."));

        // 2. ⭐️ DELETE: 조회된 엔티티를 삭제합니다.
        // 이 과정에서 DB에 DELETE 쿼리가 실행됩니다.
        todoRepository.delete(todo);

    }

    @Transactional
    public void updateTodo(
            TodoUpdateRequest tur,
            CustomMemberPrincipal customMemberPrincipal,
            Long todoId
    ) {
        Long memberId = customMemberPrincipal.getMemberId();
        Todo todo = todoRepository.findByMemberIdAndTodoId(memberId, todoId)
                .orElseThrow(() -> new NoSuchElementException("요청하신 Todo ID에 부합하는 요소를 찾을 수 없습니다."));

        todo.setContent(tur.content());
        todo.setIsDone(tur.isDone());

        todoRepository.save(todo);

    }

}
