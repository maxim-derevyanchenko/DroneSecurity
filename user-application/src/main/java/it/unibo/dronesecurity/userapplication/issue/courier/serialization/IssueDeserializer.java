package it.unibo.dronesecurity.userapplication.issue.courier.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.unibo.dronesecurity.userapplication.issue.courier.issues.*;
import it.unibo.dronesecurity.userapplication.utilities.DateHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;

/**
 * Deserialize the {@link Issue}.
 */
public class IssueDeserializer extends JsonDeserializer<Issue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Issue deserialize(final @NotNull JsonParser parser, final DeserializationContext ctxt)
            throws IOException {
        final ObjectNode root = parser.getCodec().readTree(parser);
        final String subject = root.get(IssueStringHelper.SUBJECT).asText();
        final String details = root.get(IssueStringHelper.DETAILS).asText();
        final String courierUsername = root.get(IssueStringHelper.COURIER).asText();
        final Instant sendingInstant = DateHelper.toInstant(root.get(IssueStringHelper.SENDING_INSTANT).asText());
        final String status = root.get(IssueStringHelper.STATUS).asText();
        if (root.has(IssueStringHelper.ID)) {
            final int id = root.get(IssueStringHelper.ID).asInt();

            if (IssueStringHelper.STATUS_OPEN.equals(status))
                return new OpenIssue(subject, id, details, courierUsername, sendingInstant);

            if (IssueStringHelper.STATUS_VISIONED.equals(status))
                return new VisionedIssue(subject, id, details, courierUsername, sendingInstant);

            if (IssueStringHelper.STATUS_CLOSED.equals(status)) {
                final String solution = root.get(IssueStringHelper.SOLUTION).asText();
                return new ClosedIssue(subject, id, details, courierUsername, sendingInstant, solution);
            }
        }

        return new SendingIssue(subject, details, courierUsername, sendingInstant);
    }
}
