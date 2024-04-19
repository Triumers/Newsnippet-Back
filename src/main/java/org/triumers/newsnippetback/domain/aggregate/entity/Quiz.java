package org.triumers.newsnippetback.domain.aggregate.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "tbl_quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "DATE")
    private LocalDate date;

    @Column(name = "NO")
    private int no;

    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "OPTION_A", nullable = false)
    private String optionA;

    @Column(name = "OPTION_B", nullable = false)
    private String optionB;

    @Column(name = "OPTION_C", nullable = false)
    private String optionC;

    @Column(name = "OPTION_D", nullable = false)
    private String optionD;

    @Column(name = "ANSWER", nullable = false)
    private String answer;

    @Column(name = "EXPLANATION")
    private String explanation;

    @Column(name = "NEWS_LINK", nullable = false)
    private String newsLink;

    @Column(name = "SOLVED_CNT", nullable = false)
    private int solvedCnt;

    @Column(name = "CORRECT_CNT", nullable = false)
    private int correctCnt;

    @Column(name = "CATEGORY_ID")
    private int categoryId;

    @Column(name = "ORIGIN_QUIZ_ID")
    private int originQuizId;
}
