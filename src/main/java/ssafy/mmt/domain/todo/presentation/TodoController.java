package ssafy.mmt.domain.todo.presentation;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ssafy.mmt.common.auth.CustomMemberPrincipal;
import ssafy.mmt.domain.todo.application.TodoService;
import ssafy.mmt.domain.todo.dto.request.TodoCreateRequest;
import ssafy.mmt.domain.todo.dto.request.TodoUpdateRequest;
import ssafy.mmt.domain.todo.dto.response.TodoSearchResponse;
import ssafy.mmt.domain.todo.repository.TodoRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/todo")
@Tag(name = "Todo API", description = "할일 관련 API")
public class TodoController {

    //  의존성 주입
    private final TodoService todoService;
    private final TodoRepository todoRepository;

    // [api] 할일 추가
    @PostMapping
    @Operation(summary = "할일 추가", description = "할일을 추가합니다.", security = {@SecurityRequirement(name = "JWT")})
    public ResponseEntity<Boolean> addTodo(
            @AuthenticationPrincipal CustomMemberPrincipal customMemberPrincipal,
            @RequestBody TodoCreateRequest tcr
    ) {
        todoService.addTodo(tcr, customMemberPrincipal);
        return ResponseEntity.status(200).body(true);
    }

    // [api] 할일 불러오기
    @GetMapping
    @Operation(summary = "할일 조회", description = "할일을 불러옵니다.", security = {@SecurityRequirement(name = "JWT")})
    public ResponseEntity<List<TodoSearchResponse>> getTodo(
            @AuthenticationPrincipal CustomMemberPrincipal customMemberPrincipal
    ) {
        List<TodoSearchResponse> todoList = todoService.getTodo(customMemberPrincipal);
        return ResponseEntity.status(200).body(todoList);
    }

    // [api] 할일 삭제하기
    @DeleteMapping("/{todoId}")
    @Operation(summary = "할일 삭제", description = "할일을 삭제합니다.", security = {@SecurityRequirement(name = "JWT")})
    public ResponseEntity<Boolean> deleteTodo(
            @AuthenticationPrincipal CustomMemberPrincipal customMemberPrincipal,
            @PathVariable Long todoId
    ) {
        todoService.deleteTodo(customMemberPrincipal, todoId);
        return ResponseEntity.status(200).body(true);
    }

    // [api] 할일 수정하기
    @PutMapping("/{todoId}")
    @Operation(summary = "할일 수정", description = "할일을 수정합니다.", security = {@SecurityRequirement(name = "JWT")})
    public ResponseEntity<Boolean> updateTodo(
            @RequestBody TodoUpdateRequest tur,
            @AuthenticationPrincipal CustomMemberPrincipal customMemberPrincipal,
            @PathVariable Long todoId
    ) {
        todoService.updateTodo(tur, customMemberPrincipal, todoId);
        return ResponseEntity.status(200).body(true);
    }

}
