package com.intershop.tool.architecture.report.cmd;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intershop.tool.architecture.akka.actors.tooling.AkkaMessage;
import com.intershop.tool.architecture.report.common.messages.PrintResponse;
import com.intershop.tool.architecture.report.server.ServerActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import scala.concurrent.duration.Duration;

public class ArchitectureReport
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ArchitectureReport.class);
    private static boolean buildFailed = false;

    /**
     *
     * @param args
     * <ul>
     *            <li>server ivy file: d:\Source\ish\gradle_trunk\server\share\ivy.xml</li>
     *            <li>cartridge directory: d:\Source\ish\gradle_trunk\server\share\system\cartridges</li>
     *            <li>api baseline resource e.g. api_definition_baseline_7.7.xml</li>
     * </ul>
     */
    public static void main(String[] args)
    {
        if (ArchitectureReport.validateArchitecture(args))
        {
            System.exit(1);
        }
    }

    /**
     * @param ivyFile
     * @param cartridgesDirectory
     * @param baselineURI
     * @param outputDirectory
     * @param knownIssuesURI
     * @param keySelector
     * @param args program arguments
     * @return true in case of an error
     */
    public static boolean validateArchitecture(File outputDirectory, File ivyFile, File cartridgesDirectory, File baselineFile, File knownIssuesFile, List<String> keySelector)
    {
        return ArchitectureReport.validateArchitecture(
                        "-" + ArchitectureReportConstants.ARG_IVYFILE, ivyFile.getAbsolutePath(),
                        "-" + ArchitectureReportConstants.ARG_CARTRIDGE_DIRECTORY, cartridgesDirectory.getAbsolutePath(),
                        "-" + ArchitectureReportConstants.ARG_BASELINE, baselineFile.toURI().toString(),
                        "-" + ArchitectureReportConstants.ARG_OUTPUT_DIRECTORY, outputDirectory.getAbsolutePath(),
                        "-" + ArchitectureReportConstants.ARG_EXISTING_ISSUES_FILE, knownIssuesFile.toURI().toString(),
                        "-" + ArchitectureReportConstants.ARG_KEYS, String.join(",", keySelector));
    }

    /**
     * @param args program arguments
     * @return true in case of an error
     */
    public static boolean validateArchitecture(String... args)
    {
        CommandLineArguments info = new CommandLineArguments(args);
        final ActorSystem system = ActorSystem.create("ArchitectureReport");
        // Create the 'greeter' actor
        final ActorRef serverActor = system.actorOf(Props.create(ServerActor.class), "server");

        // Create the "actor-in-a-box"
        final Inbox inbox = Inbox.create(system);
        inbox.send(serverActor, info);
        inbox.send(serverActor, AkkaMessage.TERMINATE.FLUSH_REQUEST);
        try
        {
            while(processResponse(inbox, info))
            {
                // wait until messages are processed
            }
        }
        catch(IOException | JAXBException | TimeoutException e)
        {
            LOGGER.error("Error during architecture report", e);
            buildFailed = true;
        }
        finally
        {
            system.shutdown();
        }
        return buildFailed;
    }

    private static boolean processResponse(final Inbox inbox, CommandLineArguments info)
                    throws IOException, JAXBException, TimeoutException
    {
        Object message = inbox.receive(Duration.create(5, TimeUnit.MINUTES));
        if (message instanceof String)
        {
            LOGGER.error("message: {}", message);
        }
        else if (AkkaMessage.TERMINATE.FLUSH_RESPONSE.equals(message))
        {
            LOGGER.info("REPORT FINISH");
            return false;
        }
        else if (message instanceof PrintResponse)
        {
            if (!((PrintResponse)message).getIssues().isEmpty())
            {
                ArchitectureReportOutputFolder folders = new ArchitectureReportOutputFolder(
                                info.getArgument(ArchitectureReportConstants.ARG_OUTPUT_DIRECTORY));
                LOGGER.error("Architecture report contains new errors, see '{}'.",
                                folders.getNewIssuesFile().getAbsolutePath());
                buildFailed = true;
            }
        }
        return true;
    }
}
