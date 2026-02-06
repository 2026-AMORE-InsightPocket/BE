package com.pocketmon.insightpocket.domain.ranking.service;

import com.pocketmon.insightpocket.domain.ranking.dto.RankingCurrentResponse;
import com.pocketmon.insightpocket.domain.ranking.repository.RankingRepository;
import com.pocketmon.insightpocket.global.exception.CustomException;
import com.pocketmon.insightpocket.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingCurrentService {

    private final RankingRepository rankingRepository;

    public RankingCurrentResponse getCurrentRanking(long categoryId) {

        var result = rankingRepository.findCurrentRanking(categoryId);

        if (result.items().isEmpty()) {
            throw new CustomException(ErrorCode.RANKING_NOT_FOUND);
        }

        String snapshotTime =
                result.snapshotTime() == null
                        ? null
                        : result.snapshotTime().toString().replace('T', ' ');

        return new RankingCurrentResponse(
                categoryId,
                snapshotTime,
                result.items()
        );
    }
}