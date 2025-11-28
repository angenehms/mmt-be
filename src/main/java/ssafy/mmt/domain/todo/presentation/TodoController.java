package ssafy.mmt.domain.todo.presentation;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssafy.mmt.common.auth.CustomMemberPrincipal;
import ssafy.mmt.domain.todo.application.TodoService;
import ssafy.mmt.domain.todo.dto.request.TodoCreateRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/todo")
@Tag(name = "Todo API", description = "할일 관련 API")
public class TodoController {

    //  의존성 주입
    private final TodoService todoService;

    // [api] 할일 추가
    @PostMapping
    @Operation(summary = "할일 추가", description = "할일을 추가합니다.")
    public ResponseEntity<Boolean> addTodo(
            @AuthenticationPrincipal CustomMemberPrincipal customMemberPrincipal,
            @RequestBody TodoCreateRequest tcr
    ) {
        todoService.addTodo(tcr, customMemberPrincipal);
        return ResponseEntity.status(200).body(true);
    }

}
