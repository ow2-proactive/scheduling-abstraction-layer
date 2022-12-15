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
package org.ow2.proactive.sal.service.service.application;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ow2.proactive.sal.service.util.TemporaryFilesHelper;
import org.ow2.proactive.sal.service.util.Utils;
import org.ow2.proactive.scheduler.common.job.JobVariable;
import org.ow2.proactive.scheduler.common.task.ScriptTask;
import org.ow2.proactive.scheduler.common.task.TaskVariable;
import org.ow2.proactive.scripting.InvalidScriptException;
import org.ow2.proactive.scripting.SelectionScript;
import org.ow2.proactive.scripting.SimpleScript;
import org.ow2.proactive.scripting.TaskScript;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class PAFactory {

    private PAFactory() {
    }

    /**
     * Create a simple script
     * @param implementation The script implementation
     * @param scriptLanguage The script language
     * @return A ProActive SimpleScript instance
     */
    public static SimpleScript createSimpleScript(String implementation, String scriptLanguage) {
        LOGGER.debug("Creating a simple script from its implementation ");
        SimpleScript mySQLSimpleScript = new SimpleScript(implementation, scriptLanguage);
        LOGGER.debug("Simple script created.");
        return mySQLSimpleScript;
    }

    /**
     * Create a simple script from a file
     * @param scriptFileName The script implementation file name
     * @param scriptLanguage The script language
     * @return A ProActive SimpleScript instance
     */
    public static SimpleScript createSimpleScriptFromFIle(String scriptFileName, String scriptLanguage) {
        String script = Utils.getContentWithFileName(scriptFileName);
        SimpleScript mySQLSimpleScript = createSimpleScript(script, scriptLanguage);
        LOGGER.debug("Simple script created.");
        return mySQLSimpleScript;
    }

    /**
     * Create a script task
     * @param taskName The name of the task
     * @param implementationScript The script implementation
     * @param scriptLanguage The script language
     * @return A ProActive ScriptTask instance
     */
    public static ScriptTask createScriptTask(String taskName, String implementationScript, String scriptLanguage) {
        ScriptTask scriptTask = new ScriptTask();
        scriptTask.setName(taskName);
        LOGGER.debug("Creating a bash script task");
        SimpleScript simpleScript = createSimpleScript(implementationScript, scriptLanguage);
        TaskScript taskScript = new TaskScript(simpleScript);
        LOGGER.debug("Bash script task created.");
        scriptTask.setScript(taskScript);
        return scriptTask;
    }

    /**
     * Create a groovy script task
     * @param taskName The name of the task
     * @param implementationScript The script implementation
     * @return A ProActive ScriptTask instance
     */
    public static ScriptTask createGroovyScriptTask(String taskName, String implementationScript) {
        return createScriptTask(taskName, implementationScript, "groovy");
    }

    /**
     * Create a Bash script task
     * @param taskName The name of the task
     * @param implementationScript The script implementation
     * @return A ProActive ScriptTask instance
     */
    public static ScriptTask createBashScriptTask(String taskName, String implementationScript) {
        return createScriptTask(taskName, implementationScript, "bash");
    }

    /**
     * Create a groovy script task
     * @param taskName The name of the task
     * @param scriptFileName The script implementation file name
     * @return A ProActive ScriptTask instance
     */
    public static ScriptTask createGroovyScriptTaskFromFile(String taskName, String scriptFileName) {
        return createScriptTaskFromFile(taskName, scriptFileName, "groovy");
    }

    /**
     * Create a Bash script task
     * @param taskName The name of the task
     * @param scriptFileName The script implementation file name
     * @return A ProActive ScriptTask instance
     */
    public static ScriptTask createBashScriptTaskFromFile(String taskName, String scriptFileName) {
        return createScriptTaskFromFile(taskName, scriptFileName, "bash");
    }

    /**
     * Create a script task
     * @param taskName The name of the task
     * @param scriptFileName The script implementation file name
     * @param scriptLanguage The script language
     * @return A ProActive ScriptTask instance
     */
    private static ScriptTask createScriptTaskFromFile(String taskName, String scriptFileName, String scriptLanguage) {
        ScriptTask scriptTask = new ScriptTask();
        scriptTask.setName(taskName);
        LOGGER.debug("Creating a bash script task from the file : " + scriptFileName);
        SimpleScript simpleScript = createSimpleScriptFromFIle(scriptFileName, scriptLanguage);
        TaskScript taskScript = new TaskScript(simpleScript);
        LOGGER.debug("Bash script task created.");
        scriptTask.setScript(taskScript);
        return scriptTask;
    }

    /**
     * Create a script task
     * @param taskName The name of the task
     * @param scriptFileName The script implementation file name
     * @param scriptLanguage The script language
     * @param preScriptFileName The pre-script implementation file name
     * @param preScriptLanguage The pre-script language
     * @param postScriptFileName The post-script implementation file name
     * @param postScriptLanguage The post-script language
     * @return A ProActive ScriptTask instance
     */
    public static ScriptTask createComplexScriptTaskFromFiles(String taskName, String scriptFileName,
            String scriptLanguage, String preScriptFileName, String preScriptLanguage, String postScriptFileName,
            String postScriptLanguage) {
        ScriptTask scriptTask = new ScriptTask();
        scriptTask.setName(taskName);
        LOGGER.debug("Creating a script task from the files : scriptFileName=" + scriptFileName +
                     " preScriptFileName=" + preScriptFileName + " postScriptFileName=" + postScriptFileName);
        TaskScript taskScript = new TaskScript(createSimpleScriptFromFIle(scriptFileName, scriptLanguage));
        TaskScript taskPreScript = new TaskScript(createSimpleScriptFromFIle(preScriptFileName, preScriptLanguage));
        TaskScript taskPostScript = new TaskScript(createSimpleScriptFromFIle(postScriptFileName, postScriptLanguage));
        LOGGER.debug("Bash script task created.");
        scriptTask.setScript(taskScript);
        scriptTask.setPreScript(taskPreScript);
        scriptTask.setPostScript(taskPostScript);
        return scriptTask;
    }

    /**
     * Create a Groovy node selection script
     * @param scriptFileName The script implementation file name
     * @param parameters The selection script parameters
     * @return A ProActive SelectionScript instance
     * @throws IOException In case an IOException is thrown
     */
    public static SelectionScript createGroovySelectionScript(String scriptFileName, String[] parameters)
            throws IOException {
        SelectionScript selectionScript = null;
        LOGGER.debug("Creating a groovy selection script");
        File scriptFile;
        scriptFile = TemporaryFilesHelper.createTempFileFromResource(scriptFileName);
        try {
            selectionScript = new SelectionScript(scriptFile, parameters);
        } catch (InvalidScriptException ie) {
            LOGGER.error("ERROR: Selection script not created due to an InvalidScriptException: " + ie.toString());
        }
        LOGGER.debug("Groovy selection script created.");
        TemporaryFilesHelper.delete(scriptFile);
        return selectionScript;
    }

    /**
     * Create a Map of variables of (String (key), TaskVariable (value))
     * @param variables A Map of variables in (String (key), String (value))
     * @return A Map of variables in (String, TaskVariable)
     */
    public static Map<String, TaskVariable> variablesToTaskVariables(Map<String, String> variables) {
        Map<String, TaskVariable> taskVariables = new HashMap<>();
        variables.forEach((k, v) -> taskVariables.put(k, new TaskVariable(k, v)));
        return taskVariables;
    }

    /**
     * Create a Map of variables of (String (key), JobVariable (value))
     * @param variables A Map of variables in (String (key), String (value))
     * @return A Map of variables in (String, JobVariable)
     */
    public static Map<String, JobVariable> variablesToJobVariables(Map<String, String> variables) {
        Map<String, JobVariable> jobVariables = new HashMap<>();
        variables.forEach((k, v) -> jobVariables.put(k, new TaskVariable(k, v)));
        return jobVariables;
    }
}
