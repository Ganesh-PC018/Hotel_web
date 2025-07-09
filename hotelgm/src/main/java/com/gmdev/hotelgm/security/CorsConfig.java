package com.gmdev.hotelgm.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/*
Browsers enforce the same origin policy which by default jS on https//site.A.com from making AJAX calls to api.site,B CORs
is a controlled relaxation of that policy; your server explicitly tells browser "I trust requests coming from these origins, and here are method
I allow.


CORS --> Cross Origin Resource Sharing Configuration


 "/**"--> apply to all endpoints.
Allow Only Http Methods-->GET PUT DELETE POST
Allow Request from any origin

 */

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET","POST","PUT","DELETE")
                        .allowedOrigins("*");

            }
        };
    }
}
