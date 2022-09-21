/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.sal.service.util;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.hibernate.CacheMode;
import org.hibernate.jpa.QueryHints;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class EntityManagerHelper {

    private static final EntityManagerFactory emf;

    private static final ThreadLocal<EntityManager> threadLocal;

    static {
        Map<String, String> persistenceConfiguration = new HashMap<>();
        try {
            // Load the persistence configurations
            persistenceConfiguration = PersistenceConfiguration.getAllPersistenceConfigurationPropertiesAsMap();
            LOGGER.info("Initializing the persistence with custom configurations...");

        } catch (ConfigurationException | NullPointerException e) {
            /*
             * In case the properties file was not found, this catch statement is triggered
             * The EMF will be initialized with default configurations
             *
             * The NullPointerException is triggered if the environment variable to locate the
             * properties file
             * is not found
             */
            LOGGER.info("Initializing the persistence with default configurations...");
        }

        emf = Persistence.createEntityManagerFactory("model", persistenceConfiguration);
        LOGGER.info("Initializing complete!");
        threadLocal = new ThreadLocal<EntityManager>();
    }

    public static EntityManager getEntityManager() {
        EntityManager em = threadLocal.get();

        if (em == null) {
            em = emf.createEntityManager();
            // set your flush mode here
            em.setFlushMode(FlushModeType.COMMIT);
            threadLocal.set(em);
        }
        return em;
    }

    public static void closeEntityManager() {
        EntityManager em = threadLocal.get();
        if (em != null) {
            em.close();
            threadLocal.set(null);
        }
    }

    public static void closeEntityManagerFactory() {
        emf.close();
    }

    public static void begin() {
        if (!getEntityManager().getTransaction().isActive())
            getEntityManager().getTransaction().begin();
    }

    public static void persist(Object entity) {
        getEntityManager().persist(entity);
    }

    public static void merge(Object entity) {
        getEntityManager().merge(entity);
    }

    public static void refresh(Object entity) {
        getEntityManager().refresh(entity);
    }

    public static void remove(Object entity) {
        getEntityManager().remove(entity);
    }

    public static <T> T find(Class<T> entityClass, Object primaryKey) {
        return getEntityManager().find(entityClass, primaryKey);
    }

    public static <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return getEntityManager().createQuery(qlString, resultClass).setHint(QueryHints.HINT_CACHE_MODE,
                                                                             CacheMode.REFRESH);
    }

    public static void rollback() {
        getEntityManager().getTransaction().rollback();
    }

    public static void commit() {
        getEntityManager().getTransaction().commit();
    }
}
