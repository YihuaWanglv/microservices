package com.iyihua.microservices.service.registry.conf;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.iyihua.microservices.service.registry.core.ServiceRegistry;
import com.iyihua.microservices.service.registry.core.ServiceRegistryImpl;

@Configuration
@ConfigurationProperties(prefix = "registry")
public class RegistryConfig {

	private String servers;

	@Bean
	public ServiceRegistry serviceRegistry() {
		return new ServiceRegistryImpl(servers);
	}

	public void setServers(String servers) {
		this.servers = servers;
	}
}
