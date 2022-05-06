package it.unibo.dronesecurity.userapplication.negligence.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.unibo.dronesecurity.userapplication.negligence.entities.BaseNegligenceReport;
import it.unibo.dronesecurity.userapplication.negligence.entities.ClosedNegligenceReport;
import it.unibo.dronesecurity.userapplication.negligence.entities.NegligenceReport;
import it.unibo.dronesecurity.userapplication.negligence.entities.OpenNegligenceReport;
import it.unibo.dronesecurity.userapplication.negligence.utilities.NegligenceConstants;
import it.unibo.dronesecurity.userapplication.utilities.DateHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;

/**
 * Deserialize {@link NegligenceReport} into {@link OpenNegligenceReport} or {@link ClosedNegligenceReport}.
 */
public final class NegligenceReportDeserializer extends JsonDeserializer<NegligenceReport> {

    @Override
    public @NotNull NegligenceReport deserialize(@NotNull final JsonParser parser, final DeserializationContext ctx)
            throws IOException {
        final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        final ObjectNode root = mapper.readTree(parser);

        final String negligent = root.get(NegligenceConstants.NEGLIGENT).asText();
        final String assignee = (String) ctx.getAttribute(NegligenceConstants.ASSIGNEE);
        final String maintainer = assignee == null ? root.get(NegligenceConstants.ASSIGNEE).asText() : assignee;

        final JsonNode data = root.get(NegligenceConstants.DATA);
        final double proximity = data.get(NegligenceConstants.PROXIMITY).asDouble();
        final JsonNode accelerometerData = data.get(NegligenceConstants.ACCELEROMETER);

        final BaseNegligenceReport.Builder builder = new BaseNegligenceReport.Builder(negligent, maintainer)
                .withProximity(proximity)
                .withAccelerometerData(accelerometerData);

        final boolean isClosed = root.has(NegligenceConstants.CLOSING_INSTANT);
        if (isClosed) {
            final Instant closingInstant =
                    DateHelper.toInstant(root.get(NegligenceConstants.CLOSING_INSTANT).asText());
            return builder.closed(closingInstant).build();
        } else
            return builder.build();
    }

}
