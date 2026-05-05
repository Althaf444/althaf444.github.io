package com.mint.auth.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/dash_board.html");
        registry.addViewController("/login").setViewName("forward:/login.html");
        registry.addViewController("/mfa").setViewName("forward:/mfa.html");
        registry.addViewController("/transaction").setViewName("forward:/transaction.html");
        registry.addViewController("/budgeting").setViewName("forward:/budgeting.html");
    }
}
