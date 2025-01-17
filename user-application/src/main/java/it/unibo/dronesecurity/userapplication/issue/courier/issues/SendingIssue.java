package it.unibo.dronesecurity.userapplication.issue.courier.issues;

import it.unibo.dronesecurity.userapplication.issue.courier.serialization.IssueStringHelper;

import java.time.Instant;

/**
 * Entity representing an issue report that has to be sent and created.
 */
public class SendingIssue implements Issue {

    private final String subject;
    private final String issueInfo;
    private final String courierUsername;
    private final Instant sendingTime;

    /**
     * Builds a new issue report.
     * @param subject the short description of the issue (alias title/subject)
     * @param issueInfo the information regarding the issue report
     * @param courierUsername username of the courier who sends the issue
     * @param sendingTime the timestamp of when the issue was sent
     */
    public SendingIssue(final String subject,
                        final String issueInfo,
                        final String courierUsername,
                        final Instant sendingTime) {
        this.subject = subject;
        this.issueInfo = issueInfo;
        this.courierUsername = courierUsername;
        this.sendingTime = sendingTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubject() {
        return this.subject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDetails() {
        return this.issueInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCourier() {
        return this.courierUsername;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instant getReportingDate() {
        return this.sendingTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return IssueStringHelper.STATUS_OPEN;
    }


}
