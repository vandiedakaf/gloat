package com.vdda.contest;

import com.vdda.domain.jpa.ContestOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContestProcessorFactory {

    private WinProcessor winProcessor;
    private LossProcessor lossProcessor;
    private DrawProcessor drawProcessor;

    @Autowired
    public ContestProcessorFactory(WinProcessor winProcessor, LossProcessor lossProcessor, DrawProcessor drawProcessor) {
        this.winProcessor = winProcessor;
        this.lossProcessor = lossProcessor;
        this.drawProcessor = drawProcessor;
    }

    ContestProcessor getContestProcessor(ContestOutcome contestOutcome) {
        if (ContestOutcome.WIN.equals(contestOutcome)) {
            return winProcessor;
        } else if (ContestOutcome.LOSS.equals(contestOutcome)) {
            return lossProcessor;
        } else if (ContestOutcome.DRAW.equals(contestOutcome)) {
            return drawProcessor;
        } else {
            throw new IllegalArgumentException("A contest outcome is required.");
        }
    }
}
