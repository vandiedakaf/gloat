package com.vdda.contest;

import com.vdda.domain.jpa.Contest;
import com.vdda.domain.repository.ContestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public void processContests() {
        List<Contest> contests = contestRepository.findByProcessedOrderByCreatedAsc(false);

        contests.forEach(c -> contestProcessorFactory.getContestProcessor(c.getContestOutcome()).processContest(c));
    }

}
