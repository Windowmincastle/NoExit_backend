package com.E1i3.NoExit.domain.findboard.service;

import com.E1i3.NoExit.domain.common.domain.DelYN;
import com.E1i3.NoExit.domain.findboard.domain.FindBoard;
import com.E1i3.NoExit.domain.findboard.dto.FindBoardListResDto;
import com.E1i3.NoExit.domain.findboard.dto.FindBoardResDto;
import com.E1i3.NoExit.domain.findboard.dto.FindBoardSaveReqDto;
import com.E1i3.NoExit.domain.findboard.dto.FindBoardUpdateReqDto;
import com.E1i3.NoExit.domain.findboard.repository.FindBoardRepository;
import com.E1i3.NoExit.domain.member.domain.Member;
import com.E1i3.NoExit.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;

@Transactional
@Service
public class FindBoardService {

    private final FindBoardRepository findBoardRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public FindBoardService(FindBoardRepository findBoardRepository, MemberRepository memberRepository) {
        this.findBoardRepository = findBoardRepository;
        this.memberRepository = memberRepository;
    }

    public void findBoardCreate(FindBoardSaveReqDto findBoardSaveReqDto) {

        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("회원 가입 후 글 작성이 가능합니다."));

        FindBoard findBoard = findBoardSaveReqDto.toEntity(member);
        findBoardRepository.save(findBoard);
    }

    @Transactional(readOnly = true)
    public Page<FindBoardListResDto> findBoardListResDto(Pageable pageable) {

        Page<FindBoard> findBoards = findBoardRepository.findByDelYn(pageable, DelYN.Y);
        return findBoards.map(FindBoard::listFromEntity);
    }


    @Transactional
    public FindBoardResDto update(Long id, FindBoardUpdateReqDto dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        FindBoard findBoard = findBoardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("not found id : " + id));

        if ( !findBoard.getMember().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }

        // 삭제된 게시글인지 체크
        if (findBoard.getDelYn() == DelYN.N) {
            throw new IllegalStateException("삭제된 게시글은 수정할 수 없습니다.");
        }

        findBoard.updateFromDto(dto);
        return findBoard.ResDtoFromEntity();
    }


    @Transactional
    public String delete(Long id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        FindBoard findBoard = findBoardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
// 아래 예외를 위로 옮기는것 고려
        if ( !findBoard.getMember().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 게시글만 삭제할 수 있습니다.");
        }

        findBoard.markAsDeleted();
        return "게시글 삭제 완료";
    }

    public FindBoardResDto incrementParticipantCount(Long id) {

        FindBoard findBoard = findBoardRepository.findByIdAndDelYn(id, DelYN.Y)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("참가 신청은 로그인 후 가능합니다."));

        findBoard.incrementCurrentCount();
        return findBoard.ResDtoFromEntity();
    }

}
