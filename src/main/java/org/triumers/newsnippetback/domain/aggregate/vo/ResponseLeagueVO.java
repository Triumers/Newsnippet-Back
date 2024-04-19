package org.triumers.newsnippetback.domain.aggregate.vo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseLeagueVO {
    private String message;
    private List<Ranker> rankers;

    public void addRanker(int rank, String nickname, int correctCnt) {
        rankers.add(new Ranker(rank, nickname, correctCnt));
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ranker {
        private int rank;
        private String nickname;
        private int correctCnt;
    }
}
