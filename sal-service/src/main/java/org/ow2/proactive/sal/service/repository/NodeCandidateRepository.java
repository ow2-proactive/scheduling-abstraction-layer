/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.repository;

import java.util.List;

import org.ow2.proactive.sal.model.Cloud;
import org.ow2.proactive.sal.model.NodeCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface NodeCandidateRepository extends JpaRepository<NodeCandidate, String> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM NodeCandidate nc WHERE nc.cloud=:cloud")
    void deleteBatchNodeCandidates(@Param("cloud") Cloud cloud);
}
