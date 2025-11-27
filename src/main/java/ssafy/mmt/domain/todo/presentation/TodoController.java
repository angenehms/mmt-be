package ssafy.mmt.domain.todo.presentation;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssafy.mmt.domain.todo.application.TodoService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/todo")
@Tag(name = "Todo API", description = "할일 관련 API")
public class TodoController {

    //  의존성 주입
    private final TodoService todoService;


}
