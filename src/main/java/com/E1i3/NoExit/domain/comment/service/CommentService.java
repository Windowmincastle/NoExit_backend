package com.E1i3.NoExit.domain.comment.service;

import com.E1i3.NoExit.domain.board.domain.Board;
import com.E1i3.NoExit.domain.board.dto.BoardListResDto;
import com.E1i3.NoExit.domain.board.repository.BoardRepository;
import com.E1i3.NoExit.domain.comment.domain.Comment;
import com.E1i3.NoExit.domain.comment.domain.DelYN;
import com.E1i3.NoExit.domain.comment.dto.CommentCreateReqDto;
import com.E1i3.NoExit.domain.comment.dto.CommentListResDto;
import com.E1i3.NoExit.domain.comment.dto.CommentUpdateReqDto;
import com.E1i3.NoExit.domain.comment.repository.CommentRepository;
import com.E1i3.NoExit.domain.member.domain.Member;
import com.E1i3.NoExit.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    @Autowired
    public CommentService(CommentRepository commentRepository, MemberRepository memberRepository, BoardRepository boardRepository) {
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
    }



    public void commentCreate(CommentCreateReqDto dto) { // 댓굴 생성 보드아이디, 멤버아이디, 내용 받아옴

//        Member member = memberRepository.findById(dto.getMemberId())
//                .orElseThrow(() -> new EntityNotFoundException("없는 회원입니다.")); // 로그인하면 필요없어짐
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()-> new EntityNotFoundException("없는 회원입니다."));

        Board board = boardRepository.findById(dto.getBoardId()).orElse(null); // 보드 아이디로 보드 조회
        Comment comment = Comment.builder()
                .board(board)
                .memberId(member.getId())
                .content(dto.getContent())
                .build();

        board.addComment(comment); // 게시글 댓글 목록에 추가
        boardRepository.save(board);
        commentRepository.save(comment);
    }






    public Page<CommentListResDto> commentList(Pageable pageable){ // 댓글 조회
        Page<Comment> comments = commentRepository.findByDelYN(pageable, DelYN.N);
//        Page<CommentListResDto> commentListResDtos = comments.map(
//                a->a.fromEntity());
        Page<CommentListResDto> commentListResDtos = comments.map(Comment::fromEntity);
        return commentListResDtos;
    }





    public Comment commentUpdate(Long id, CommentUpdateReqDto dto) { // 댓글 수정
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()-> new EntityNotFoundException("없는 회원입니다."));
        if (!member.getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 댓글만 수정할 수 있습니다.");
        }
        Comment comment = commentRepository.findById(id).orElseThrow(()->new EntityNotFoundException(" 찾을 수 없습니다."));
        comment.updateEntity(dto);
        return comment;
    }



    public void commentDelete(Long id) { // 댓글 삭제
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()-> new EntityNotFoundException("없는 회원입니다."));
        if (!member.getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 댓글만 수정할 수 있습니다.");
        }
        Comment comment = commentRepository.findById(id).orElseThrow(()->new EntityNotFoundException("찾을 수 없습니다."));
        comment.deleteEntity();
//        commentRepository.delete(comment);
    }


    public int commentUpdateLikes(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("comment not found with id: " + id));
        comment.updateLikes();
        return comment.getLikes();
    }

    public int commentUpdateDislikes(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("comment not found with id: " + id));
        comment.updateDislikes();
        return comment.getDislikes();
    }
}

