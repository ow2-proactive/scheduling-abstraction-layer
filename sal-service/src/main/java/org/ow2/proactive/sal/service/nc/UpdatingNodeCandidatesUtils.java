/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.nc;

import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Component
public class UpdatingNodeCandidatesUtils {

    @Autowired
    private NodeCandidateUtils nodeCandidateUtils;

    @Async
    public Future<Boolean> asyncUpdate(List<String> newCloudIds) throws InterruptedException {
        LOGGER.info("Thread updating node candidates related to clouds " + newCloudIds.toString() + " started.");
        nodeCandidateUtils.initNodeCandidateUtils();
        nodeCandidateUtils.saveNodeCandidates(newCloudIds);
        LOGGER.info("Thread updating node candidates related to clouds " + newCloudIds.toString() + " ended properly.");
        return new AsyncResult<>(true);
    }

    @Async
    public Future<Boolean> asyncClean(List<String> newCloudIds) throws InterruptedException {
        LOGGER.info("Thread cleaning node candidates related to clouds " + newCloudIds.toString() + " started.");
        long cleaned = nodeCandidateUtils.cleanNodeCandidates(newCloudIds);
        LOGGER.info("Thread cleaning node candidates related to clouds {} ended properly with {} NC cleaned.",
                    newCloudIds.toString(),
                    cleaned);
        return new AsyncResult<>(true);
    }
}
