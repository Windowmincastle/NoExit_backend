package com.E1i3.NoExit.domain.review.domain;

import javax.persistence.*;

import com.E1i3.NoExit.domain.common.domain.BaseTimeEntity;
import com.E1i3.NoExit.domain.common.domain.DelYN;
import com.E1i3.NoExit.domain.member.domain.Member;
import com.E1i3.NoExit.domain.reservation.domain.Reservation;
import com.E1i3.NoExit.domain.review.dto.ReviewListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String content; // 한줄평

    private int rating;  // 순위

    private String imagePath; // 이미지

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DelYN delYN = DelYN.N;  // 삭제 여부

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "member_id" , nullable = false)
    private Member member;


    public void updateDelYN() {
        this.delYN = DelYN.Y;
    }

    public void updateImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    @Builder
    public Review(Long id, String content, int rating, String imagePath, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.content = content;
        this.rating = rating;
        this.imagePath = imagePath;
    }

    public static ReviewListDto fromEntity(Review review) {
        return ReviewListDto.builder()
                .id(review.getId())
                .rating(review.getRating())
                .imagePath(review.getImagePath())
                .memberNickname(review.getMember().getNickname())
                .content(review.getContent())
                .gameName(review.getReservation().getGame().getGameName())
                .createdAt(review.getCreatedTime())
                .build();
    }
}
