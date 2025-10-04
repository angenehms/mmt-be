package testApi;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Content2 API", description = "content2 API")
@RestController
@RequestMapping("/api/v1")
public class testController2 {

    @GetMapping("/content2/{id}")
    public ResponseEntity<?> contentGet(
            @PathVariable("id")Long id
    ){

        Map<String, Object> resultBody = Map.of(
                "id", id,
                "title", "제목" + id,
                "content", "내용" + id
        );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json"));

        return new ResponseEntity<>(resultBody, httpHeaders, HttpStatus.OK);
    }

//    @PostMapping("/content2")
//    public ResponseEntity<?> contentPost(
//            @RequestBody ContentRequestDTO dto
//    ) {
//
//        Map<String, Object> resultBody = Map.of("id", 1L);
//
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(new MediaType("application", "json"));
//
//        return new ResponseEntity<>(resultBody, httpHeaders, HttpStatus.OK);
//    }

    @DeleteMapping("/content2/{id}")
    public ResponseEntity<?> contentDelete(
            @PathVariable("id") Long id
    ) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json"));

        return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
    }

}

