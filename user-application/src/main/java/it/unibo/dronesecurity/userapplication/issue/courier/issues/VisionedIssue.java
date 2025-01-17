package it.unibo.dronesecurity.userapplication.issue.courier.issues;

import it.unibo.dronesecurity.userapplication.issue.courier.serialization.IssueStringHelper;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * Class representing an open issue that is currently visioned by a maintainer.
 */
public class VisionedIssue extends CreatedIssue {

    /**
     * Builds a new Visioned Issue report.
     *
     * @param subject         the short description of the issue (alias title/subject)
     * @param id              the id of the created issue
     * @param issueInfo       the information regarding the issue report
     * @param courierUsername username of the courier who sends the issue
     * @param sendingTime     the timestamp of when the issue was sent
     */
    public VisionedIssue(final String subject,
                            @NotNull final int id,
                            final String issueInfo,
                            final String courierUsername,
                            final Instant sendingTime) {
        super(subject, id, issueInfo, courierUsername, sendingTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return IssueStringHelper.STATUS_VISIONED;
    }

    /**
     * Transforms this issue in closed issue when the maintainer completes his visioning.
     * @param solution string representing the solution used by the maintainer
     * @return the new {@link ClosedIssue} from this VisionedIssue
     */
    public ClosedIssue closeIssue(final String solution) {
        return new ClosedIssue(this.getSubject(),
                this.getId(),
                this.getDetails(),
                this.getCourier(),
                this.getReportingDate(),
                solution);
    }
}
