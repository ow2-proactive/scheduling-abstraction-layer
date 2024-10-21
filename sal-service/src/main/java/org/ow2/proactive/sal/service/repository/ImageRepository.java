/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.repository;

import java.util.List;

import org.ow2.proactive.sal.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface ImageRepository extends JpaRepository<Image, String> {

    @Transactional(readOnly = true)
    @Query(value = "SELECT id FROM Image WHERE id NOT IN (SELECT image.id FROM NodeCandidate GROUP BY image.id)")
    List<String> getOrphanImageIds();

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Image WHERE id NOT IN (SELECT image.id FROM NodeCandidate GROUP BY image.id)")
    void deleteOrphanImageIds();
}
