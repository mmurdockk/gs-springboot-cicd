package com.springboot.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@PropertySource(ignoreResourceNotFound = true, value = "classpath:misc.properties")
public class GreetingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GreetingController.class);

    @Value("${app.version}")
    private String appVersion;

    @Value("${app.buildNumber}")
    private String appBuildNumber;

    @RequestMapping("/greeting")
    public @ResponseBody
    String greeting() {
        LOGGER.debug("This is a debug message");
        LOGGER.info("This is an info message");
        LOGGER.warn("This is a warn message");
        LOGGER.error("This is an error message");

        return "Hello World v " + appVersion + " - " + appBuildNumber;
    }
}
