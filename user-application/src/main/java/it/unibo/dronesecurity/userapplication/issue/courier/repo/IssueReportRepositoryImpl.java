package it.unibo.dronesecurity.userapplication.issue.courier.repo;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import it.unibo.dronesecurity.userapplication.auth.entities.Role;
import it.unibo.dronesecurity.userapplication.issue.courier.issues.*;
import it.unibo.dronesecurity.userapplication.issue.courier.serialization.IssueStringHelper;
import it.unibo.dronesecurity.userapplication.utilities.UserHelper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//TODO refactor repositories (make an abstract class for mongo initialization)
/**
 * Implementation of {@link IssueReportRepository}.
 */
public final class IssueReportRepositoryImpl implements IssueReportRepository {

    private static final String COLLECTION_NAME = "issueReports";
    private static IssueReportRepository singleton;
    private final MongoClient database;

    private IssueReportRepositoryImpl() {
        final JsonObject config = new JsonObject();
        config.put("db_name", "drone");
        //TODO move vertx instantiating to a previous phase.
        this.database = MongoClient.create(Vertx.vertx(), config);
    }

    /**
     * Get the Singleton instance.
     * @return the singleton
     */
    public static IssueReportRepository getInstance() {
        synchronized (IssueReportRepositoryImpl.class) {
            if (singleton == null)
                singleton = new IssueReportRepositoryImpl();
            return singleton;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addIssue(final Issue issue) {
        final JsonObject newIssue = new JsonObject();
        this.getLastID().onComplete(id -> {
            if (id.succeeded())
                newIssue.put(IssueStringHelper.ID, id.result() + 1);
            else
                newIssue.put(IssueStringHelper.ID, 1);
            newIssue.mergeIn(JsonObject.mapFrom(issue), true);
            this.database.save(COLLECTION_NAME, newIssue);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Boolean> visionOpenIssue(final OpenIssue issue) {
        final JsonObject newStatus = new JsonObject()
                .put(IssueStringHelper.STATUS, IssueStringHelper.STATUS_VISIONED);
        return this.updateIssue(issue.getId(), newStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Boolean> closeVisionedIssue(final VisionedIssue issue, final String solution) {
        final JsonObject update = new JsonObject()
                .put(IssueStringHelper.STATUS, IssueStringHelper.STATUS_CLOSED)
                .put(IssueStringHelper.SOLUTION, solution);

        return this.updateIssue(issue.getId(), update);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<List<CreatedIssue>> getOpenIssues() {
        final JsonObject openIssuesQuery = this.initQueryWithUserData();

        return this.getOpenIssueFromQuery(openIssuesQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<List<ClosedIssue>> getClosedIssues() {
        final JsonObject closedIssuesQuery = this.initQueryWithUserData();

        return this.getClosedIssuesFromQuery(closedIssuesQuery);
    }

    private Future<Long> getLastID() {
        final FindOptions options = new FindOptions();
        options.setSort(new JsonObject().put(IssueStringHelper.ID, -1));
        options.setLimit(1);
        return this.database.findWithOptions(COLLECTION_NAME, new JsonObject(), options)
                .map(val -> val.get(0).getLong(IssueStringHelper.ID));
    }

    private JsonObject initQueryWithUserData() {
        final JsonObject queryWithUser = new JsonObject();
        if (UserHelper.getLoggedUser().getRole() == Role.COURIER)
            queryWithUser.put(IssueStringHelper.COURIER, UserHelper.getLoggedUser().getUsername());
        else if (UserHelper.getLoggedUser().getRole() == Role.MAINTAINER)
            queryWithUser.put(IssueStringHelper.COURIER, UserHelper.getLoggedUser().getUsername());
//TODO            openIssuesQuery.put(IssueStringHelper.ASSIGNEE, UserHelper.get().getUsername());

        return queryWithUser;
    }

    private Future<List<CreatedIssue>> getOpenIssueFromQuery(final JsonObject query) {
        final JsonObject statusOpenValues = new JsonObject();
        statusOpenValues.put("$in", new JsonArray().add(IssueStringHelper.STATUS_OPEN)
                .add(IssueStringHelper.STATUS_VISIONED));
        query.put(IssueStringHelper.STATUS, statusOpenValues);

        return this.database.find(COLLECTION_NAME, query)
                .transform(issues -> {
                    List<CreatedIssue> result = Collections.emptyList();
                    if (!issues.result().isEmpty())
                        result = issues.result().stream()
                                .map(json -> Json.decodeValue(json.toString(), CreatedIssue.class))
                                .collect(Collectors.toList());

                    return Future.succeededFuture(result);
                });
    }

    private Future<List<ClosedIssue>> getClosedIssuesFromQuery(final JsonObject query) {
        query.put(IssueStringHelper.STATUS, IssueStringHelper.STATUS_CLOSED);

        return this.database.find(COLLECTION_NAME, query)
                .transform(issues -> {
                    List<ClosedIssue> result = Collections.emptyList();
                    if (!issues.result().isEmpty())
                        result = issues.result().stream()
                                .map(json -> Json.decodeValue(json.toString(), ClosedIssue.class))
                                .collect(Collectors.toList());

                    return Future.succeededFuture(result);
                });
    }

    private Future<Boolean> updateIssue(final int issueId, final JsonObject updatingValues) {
        final JsonObject query = new JsonObject();
        query.put(IssueStringHelper.ID, issueId);
        final JsonObject update = new JsonObject().put("$set", updatingValues);

        return this.database.findOneAndUpdate(COLLECTION_NAME, query, update).map(Objects::nonNull);
    }
}
