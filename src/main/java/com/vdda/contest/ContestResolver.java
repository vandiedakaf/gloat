package com.vdda.contest;

import com.vdda.jpa.Contest;
import com.vdda.repository.ContestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by francois
 * on 2017-01-14
 * for vandiedakaf solutions
 */
@Service
@Slf4j
public class ContestResolver {

    private ContestRepository contestRepository;
    private ContestProcessorFactory contestProcessorFactory;

    @Autowired
    public ContestResolver(ContestRepository contestRepository, ContestProcessorFactory contestProcessorFactory) {
        this.contestRepository = contestRepository;
        this.contestProcessorFactory = contestProcessorFactory;
    }

    @Async
    public Future<?> processContests() {
        List<Contest> contests = contestRepository.findByProcessedOrderByCreatedAsc(false);

        contests.forEach(c -> contestProcessorFactory.getContestProcessor(c.getContestOutcome()).processContest(c));

        return new AsyncResult<>(null);
    }

}
