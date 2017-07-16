package com.vdda.contest;

import com.vdda.jpa.ContestOutcome;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-02-20
 * for vandiedakaf solutions
 */
public class ContestProcessorFactoryTest {

    ContestProcessorFactory contestProcessorFactory;

    @Before
    public void setUp() throws Exception {
        contestProcessorFactory = new ContestProcessorFactory(new WinProcessor(null, null, null, null), new LossProcessor(null, null, null, null), new DrawProcessor(null, null, null, null));

    }

    @Test
    public void getContestProcessorGolden() throws Exception {
        assertThat(contestProcessorFactory.getContestProcessor(ContestOutcome.WIN), is(instanceOf(WinProcessor.class)));
        assertThat(contestProcessorFactory.getContestProcessor(ContestOutcome.LOSS), is(instanceOf(LossProcessor.class)));
        assertThat(contestProcessorFactory.getContestProcessor(ContestOutcome.DRAW), is(instanceOf(DrawProcessor.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getContestProcessorException() throws Exception {

        contestProcessorFactory.getContestProcessor(null);
    }
}
