package com.iyihua.microservices.service.registry.web;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.iyihua.microservices.service.registry.core.ServiceRegistry;
import com.iyihua.microservices.service.registry.core.ServiceRegistryImpl;

@Component
public class WebListener implements ServletContextListener {
	
	private static Logger logger = LoggerFactory.getLogger(WebListener.class);

	@Value("${server.address}")
	private String serverAddress;
	
	@Value("${server.port}")
	private int serverPort;
	
	@Autowired
	private ServiceRegistry serviceRegistry;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
		Map<RequestMappingInfo, HandlerMethod> infoMap = mapping.getHandlerMethods();
		for (RequestMappingInfo info : infoMap.keySet()) {
			String serviceName = info.getName();
			logger.info("serviceName: {}", serviceName);
			logger.info("serviceAddress: {}", String.format("%s:%d", serverAddress, serverPort));
			
			serviceRegistry.register(serviceName, String.format("%s:%d", serverAddress, serverPort));
		}
	}

}
