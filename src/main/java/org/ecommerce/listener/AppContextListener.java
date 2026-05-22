package org.ecommerce.listener;

import org.ecommerce.util.DatabaseInitializer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Logger;

/**
 * Runs on Tomcat startup before any filter or servlet is initialized.
 * Triggers automatic database schema initialization.
 */
public class AppContextListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOG.info("[FashionHub] Application starting — initializing database...");
        DatabaseInitializer.initialize();
        LOG.info("[FashionHub] Application ready.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("[FashionHub] Application shutting down.");
    }
}
