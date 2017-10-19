package com.ctrip.soa.artemis.web;

import javax.servlet.*;

import com.ctrip.soa.artemis.management.GroupDiscoveryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.DispatcherServlet;

import com.ctrip.soa.artemis.cluster.ClusterManager;
import com.ctrip.soa.artemis.cluster.NodeInitializer;
import com.ctrip.soa.artemis.discovery.DiscoveryServiceImpl;
import com.ctrip.soa.artemis.management.ManagementDiscoveryFilter;
import com.ctrip.soa.artemis.management.ManagementInitializer;
import com.ctrip.soa.artemis.management.ManagementRepository;
import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.caravan.web.filter.Filters;
import com.google.common.collect.Lists;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class WebApplicationInitializerImpl implements WebApplicationInitializer {

    private static final String APPLICATION_CONTEXT_FILE = "classpath:application-context.xml";

    private static final String DISPATCHER_SERVLET_NAME = "dispatcherServlet";
    private static final String DISPATCHER_SERVLET_MAPPING = "/*";

    private static final String HIDDEN_HTTP_METHOD_FILTER_NAME = "hiddenHttpMethodFilter";

    private static final Logger logger = LoggerFactory.getLogger(WebApplicationInitializerImpl.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.info("===== Application is starting up! ========");

        try {
            XmlWebApplicationContext appContext = new XmlWebApplicationContext();
            appContext.setConfigLocation(APPLICATION_CONTEXT_FILE);

            ServletRegistration.Dynamic registration = servletContext.addServlet(DISPATCHER_SERVLET_NAME, new DispatcherServlet(appContext));
            registration.setLoadOnStartup(1);
            registration.addMapping(DISPATCHER_SERVLET_MAPPING);

            // allow cross domain
            Filters.registerCrossDomainFilter(servletContext, DISPATCHER_SERVLET_MAPPING);

            // allow put/delete method
            servletContext.addFilter(HIDDEN_HTTP_METHOD_FILTER_NAME, new HiddenHttpMethodFilter());

            Filters.registerCompressingFilter(servletContext, DISPATCHER_SERVLET_MAPPING);

            DataConfig.init();

            DiscoveryServiceImpl.getInstance().addFilters(GroupDiscoveryFilter.getInstance(), ManagementDiscoveryFilter.getInstance());

            ManagementRepository.getInstance().addFilter(GroupDiscoveryFilter.getInstance());

            ClusterManager.INSTANCE.init(Lists.<NodeInitializer> newArrayList(ManagementInitializer.INSTANCE));
        } catch (Throwable ex) {
            logger.error("WebApplicationInitalizer failed.", ex);
            ex.printStackTrace();
            throw new ServletException(ex);
        }
    }

}