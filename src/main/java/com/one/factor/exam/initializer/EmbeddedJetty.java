package com.one.factor.exam.initializer;

import com.one.factor.exam.core.HibernateManager;
import com.one.factor.exam.entities.Grid;
import com.one.factor.exam.entities.UserPosition;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EmbeddedJetty {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedJetty.class);
    private static final int DEFAULT_PORT = 8080;
    private static final String CONTEXT_PATH = "/";
    private static final String CONFIG_LOCATION = "com.one.factor.exam.config";
    private static final String MAPPING_URL = "/*";
    private WebApplicationContext webApplicationContext = null;

    /**
     *
     * @param args have to contains two argument
     *             first is path to Grid file
     *             second is path to UserPosition file
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Have to be specified two path");
        }

        EmbeddedJetty embeddedJetty = new EmbeddedJetty();

        embeddedJetty.initTables(args);

        embeddedJetty.startJetty(DEFAULT_PORT);
    }

    private void initTables (String[] args) throws IOException {
        logger.info("Init tables from files");
        initContext();
        HibernateManager hibernateManager = getHibernateManager();

        BufferedReader br = null;

        List<Object> itemsToSave = new ArrayList<>(10000);

        if (hibernateManager.isEmpty(Grid.class)) {
            try {
                String currentLine;
                br = new BufferedReader(new FileReader(args[0]));
                while ((currentLine = br.readLine()) != null) {
                    try {
                        String[] splitted = currentLine.split(";");
                        Grid grid = new Grid();
                        grid.setTileX(Integer.parseInt(splitted[0]));
                        grid.setTileY(Integer.parseInt(splitted[1]));
                        grid.setDistanceError(Integer.parseInt(splitted[2]));
                        itemsToSave.add(grid);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                if (br != null) {
                    br.close();
                }
            }
        }

        if (hibernateManager.isEmpty(UserPosition.class)) {
            try {
                String currentLine;
                br = new BufferedReader(new FileReader(args[1]));
                while ((currentLine = br.readLine()) != null) {
                    try {
                        String[] splitted = currentLine.split(";");
                        UserPosition userPosition = new UserPosition();
                        userPosition.setId(Integer.parseInt(splitted[0]));
                        userPosition.setLat(Double.parseDouble(splitted[1]));
                        userPosition.setLon(Double.parseDouble(splitted[2]));
                        itemsToSave.add(userPosition);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                if (br != null) {
                    br.close();
                }
            }
        }

        if (!hibernateManager.batchSave(itemsToSave)) {
            throw new IllegalStateException("Cannot save items from files to DB");
        }
        logger.info("Done Init tables");
    }


    private void startJetty(int port) throws Exception {
        logger.debug("Starting server at port {}", port);
        Server server = new Server(port);
        server.setHandler(getServletContextHandler(webApplicationContext));
        server.start();
        logger.info("Server started at port {}", port);
        server.join();
    }

    private HibernateManager getHibernateManager() {
        if (webApplicationContext == null) {
            initContext();
        }
        return (HibernateManager)webApplicationContext.getBean("hibernateManager");
    }

    private ServletContextHandler getServletContextHandler(WebApplicationContext context) throws IOException {
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath(CONTEXT_PATH);
        contextHandler.addServlet(new ServletHolder(new DispatcherServlet(context)), MAPPING_URL);
        contextHandler.addEventListener(new ContextLoaderListener(context));
        return contextHandler;
    }

    private void initContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(CONFIG_LOCATION);
        context.refresh();
        webApplicationContext = context;
    }
}
