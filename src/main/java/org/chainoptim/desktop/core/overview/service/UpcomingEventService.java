package org.chainoptim.desktop.core.overview.service;

import org.chainoptim.desktop.core.overview.model.UpcomingEvent;
import org.chainoptim.desktop.shared.httphandling.Result;
import org.chainoptim.desktop.shared.search.model.SearchParams;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UpcomingEventService {

    CompletableFuture<Result<List<UpcomingEvent>>> getUpcomingEventsByOrganizationId(Integer organizationId);
    CompletableFuture<Result<List<UpcomingEvent>>> getUpcomingEventsByOrganizationIdAdvanced(
            Integer organizationId,
            SearchParams searchParams
    );
}
