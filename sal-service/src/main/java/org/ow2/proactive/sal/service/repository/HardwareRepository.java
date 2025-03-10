/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.repository;

import java.util.List;

import org.ow2.proactive.sal.model.Hardware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface HardwareRepository extends JpaRepository<Hardware, String> {

    @Transactional(readOnly = true)
    @Query("SELECT h FROM Hardware h WHERE h.id LIKE CONCAT(:cloudId, '%')")
    List<Hardware> findByCloudId(String cloudId);

    @Transactional(readOnly = true)
    @Query(value = "SELECT id FROM Hardware WHERE id NOT IN (SELECT hardware.id FROM NodeCandidate GROUP BY hardware.id)")
    List<String> getOrphanHardwareIds();

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Hardware WHERE id NOT IN (SELECT hardware.id FROM NodeCandidate GROUP BY hardware.id)")
    void deleteOrphanHardwareIds();
}
