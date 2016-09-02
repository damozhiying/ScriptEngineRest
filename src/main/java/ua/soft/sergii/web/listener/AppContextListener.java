package ua.soft.sergii.web.listener;

import ua.soft.sergii.ApplicationConstants;
import ua.soft.sergii.container.ThreadPoolContainer;
import ua.soft.sergii.container.ScriptsContainer;
import ua.soft.sergii.executor.ScriptExecutorFactory;
import ua.soft.sergii.service.mock.MockIntegerTokenService;
import ua.soft.sergii.service.mock.MockScriptServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        setMockContextParams(servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {}

    private void setMockContextParams(ServletContext servletContext) {
        servletContext.setAttribute(ApplicationConstants.ACCESS_TOKEN_SERVICE,
                new MockIntegerTokenService());
        servletContext.setAttribute(ApplicationConstants.SCRIPT_SERVICE,
                new MockScriptServiceImpl( new ScriptsContainer(),
                        new ScriptExecutorFactory(new ThreadPoolContainer())));
    }
}
