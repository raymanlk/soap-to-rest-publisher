package com.soap.rest;

import io.prometheus.client.CollectorRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@Import({PrometheusScrapeEndpoint.class, CollectorRegistry.class})
public class BusinessTemplateApplication {

	public static void main(String[] args) {
		try {
			setHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		SpringApplication.run(BusinessTemplateApplication.class, args);
	}
	private static void setHostAddress() throws UnknownHostException {
		InetAddress ip = InetAddress.getLocalHost();
		String hostAddress = ip.getHostAddress();
		System.setProperty("host.address",hostAddress);
	}


}
