package it.unibo.dronesecurity.userapplication.issue.courier.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibo.dronesecurity.userapplication.issue.courier.issues.ClosedIssue;
import it.unibo.dronesecurity.userapplication.issue.courier.issues.Issue;
import it.unibo.dronesecurity.userapplication.utilities.CastHelper;
import it.unibo.dronesecurity.userapplication.utilities.DateHelper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Serializes the {@link Issue}.
 */
public class IssueSerializer extends JsonSerializer<Issue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final @NotNull Issue value, final @NotNull JsonGenerator gen,
                          final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeStringField(IssueStringHelper.SUBJECT, value.getSubject());
        gen.writeStringField(IssueStringHelper.DETAILS, value.getDetails());
        gen.writeStringField(IssueStringHelper.COURIER, value.getCourier());
        gen.writeStringField(IssueStringHelper.SENDING_INSTANT, DateHelper.toString(value.getReportingDate()));
        gen.writeStringField(IssueStringHelper.STATUS, value.getState());

        final Optional<ClosedIssue> closedIssue = CastHelper.safeCast(value, ClosedIssue.class);
        closedIssue.ifPresent(issue -> {
            try {
                gen.writeStringField(IssueStringHelper.SOLUTION, issue.getIssueSolution());
            } catch (IOException e) {
                LoggerFactory.getLogger(getClass()).error("Error occurred during serialization!", e);
            }
        });
    }
}
