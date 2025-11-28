package ssafy.mmt.domain.member.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ssafy.mmt.common.auth.CustomMemberPrincipal;
import ssafy.mmt.domain.member.application.MemberService;
import ssafy.mmt.domain.member.dto.request.MemberVaildRequest;
import ssafy.mmt.domain.member.dto.response.MemberInfoResponse;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/member")
@Tag(name = "Member API", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 자체 로그인 유저 존재 확인
    @PostMapping(value = "/exist", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> isMemberExist(
            @Validated(MemberVaildRequest.existGroup.class)
            @RequestBody MemberVaildRequest mvr
    ) {
        return ResponseEntity.ok(memberService.isMemberExist(mvr));
    }

    // 회원가입
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> join (
            @Validated(MemberVaildRequest.addGroup.class)
            @RequestBody MemberVaildRequest mvr
    ) {
        Long memberId = memberService.createMember(mvr); // 회원가입 완료 후 id 값을 받게됨
        Map<String, Long> responseBody = Collections.singletonMap("memberId", memberId);
        return ResponseEntity.status(201).body(responseBody);
    }

    // 유저 정보
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(security = {@SecurityRequirement(name = "JWT")})
    public MemberInfoResponse userMeApi(
            @AuthenticationPrincipal CustomMemberPrincipal customMemberPrincipal
    ) throws AccessDeniedException {
        Long memberId = customMemberPrincipal.getMemberId();
        return memberService.readMember(memberId);
    }

    // 유저 수정 (자체 로그인 유저만)
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(security = {@SecurityRequirement(name = "JWT")})
    public ResponseEntity<Long> updateUserApi(
            @AuthenticationPrincipal CustomMemberPrincipal customMemberPrincipal,
            @Validated(MemberVaildRequest.updateGroup.class)
            @RequestBody MemberVaildRequest mvr
    ) throws AccessDeniedException {
        Long memberId = customMemberPrincipal.getMemberId();
        return ResponseEntity.status(200).body(memberService.updateMember(mvr, memberId));
    }

    // 유저 제거 (자체/소셜)
    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(security = {@SecurityRequirement(name = "JWT")})
    public ResponseEntity<Boolean> deleteUserApi(
            @AuthenticationPrincipal CustomMemberPrincipal customMemberPrincipal,
            @Validated(MemberVaildRequest.deleteGroup.class)
            @RequestBody MemberVaildRequest mvr
    ) throws AccessDeniedException {
        Long memberId = customMemberPrincipal.getMemberId();
        memberService.deleteMember(mvr, memberId);
        return ResponseEntity.status(200).body(true);
    }
}
