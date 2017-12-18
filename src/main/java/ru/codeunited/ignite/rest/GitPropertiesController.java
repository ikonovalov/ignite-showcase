package ru.codeunited.ignite.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@RestController
@RequestMapping("/git")
public class GitPropertiesController {

    @GetMapping
    public Properties get() throws IOException {
        Properties properties = new Properties();
        InputStream resourceAsStream = getClass().getResourceAsStream("/git.properties");
        properties.load(resourceAsStream);
        return properties;
    }

}
