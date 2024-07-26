package com.E1i3.NoExit.domain.member.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.E1i3.NoExit.domain.common.auth.JwtTokenProvider;
import com.E1i3.NoExit.domain.mail.service.MailVerifyService;
import com.E1i3.NoExit.domain.member.domain.Member;
import com.E1i3.NoExit.domain.common.dto.CommonResDto;

import com.E1i3.NoExit.domain.member.dto.MemberListResDto;
import com.E1i3.NoExit.domain.member.dto.MemberLoginReqDto;
import com.E1i3.NoExit.domain.member.dto.MemberSaveReqDto;
import com.E1i3.NoExit.domain.member.dto.MemberUpdateDto;
import com.E1i3.NoExit.domain.member.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MemberController {

	@Autowired
	private final MemberService memberService;
	@Autowired
	private MailVerifyService mailVerifyService;
	@Autowired
	private final JwtTokenProvider jwtTokenProvider;

	public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
		this.memberService = memberService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	// 회원가입 /member/create
	@PostMapping("/member/create")
	public ResponseEntity<CommonResDto> memberCreatePost(@RequestBody MemberSaveReqDto dto) {
		CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "회원가입 성공", memberService.memberCreate(dto).getId());
		return new ResponseEntity<>(commonResDto, HttpStatus.OK);
	}

	// // 인증번호 검증 요청
	// @GetMapping("/email/requestCode")
	// public ResponseEntity<CommonResDto> verificationEmail(@RequestParam("email") @Valid String email,
	// 	@RequestParam("code") String authCode) {
	// 	boolean response = mailVerifyService.verifiedCode(email, authCode);
	// 	if (!response) {
	// 		// 	일치하지 않는다면 MariaDB 삭제
	// 		memberService.memberDelete(email);
	// 	}
	// 	CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "이메일 인증 성공", response);
	// 	return new ResponseEntity<>(commonResDto, HttpStatus.OK);
	// }

	// 상세 내역 수정
	@PostMapping("/member/update")
	public ResponseEntity<CommonResDto> updateMember(@RequestBody MemberUpdateDto dto) {
		CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "회원정보 수정", memberService.memberUpdate(dto).getId());
		return new ResponseEntity<>(commonResDto, HttpStatus.OK);
	}

	// 탈퇴 (?)
	@PostMapping("/member/delete")
	public ResponseEntity<CommonResDto> deleteMember(@RequestBody MemberUpdateDto dto) {
		CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "회원정보 삭제",  memberService.memberDelete(dto.getEmail()).getId());
		return new ResponseEntity<>(commonResDto, HttpStatus.OK);
	}

	// 회원 리스트
	@GetMapping("/member/list")
	public ResponseEntity<Object> memberList(Pageable pageable) {
		Page<MemberListResDto> members = memberService.memberList(pageable);
		return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "member List", members.toList()), HttpStatus.OK);
	}

	// 회원 정보(마이페이지) 조회하는 api필요 -> 토큰으로 처리

	@PostMapping("/doLogin")
	public ResponseEntity<Object> doLogin(@RequestBody MemberLoginReqDto memberLoginReqDto) {
		// email, password가 일치하는지 검증
		Member member = memberService.login(memberLoginReqDto);

		// 	일치하는 경우 accessToken 생성
		String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
		Map<String, Object> loginInfo = new HashMap<>();
		loginInfo.put("id", member.getId());
		loginInfo.put("token", jwtToken);

		// 생성된 토큰을 comonResDto에 담아서 사용자에게 리턴
		return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "member login", loginInfo), HttpStatus.OK);
	}
}
