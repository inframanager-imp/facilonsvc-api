
package com.facilon.app.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.apache.commons.lang3.RandomUtils;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import java.util.HashMap;
import java.util.Map;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {

    int lowerBound = 32;
    int upperBound = 100;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("facilon").version("1.0").description("facilon")
                .termsOfService("http://facilon").license(new License().name("Apache 2.0").url("http://facilon")));
    }

    /**
     * According to the sorting on @Tag, write x-order
     *
     * @return the global open api customizer
     */
    @Bean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer ()  {
        return  openApi  ->  {
            if  ( openApi . getTags ()!= null ){
                openApi . getTags () . forEach ( tag  ->  {
                    Map< String , Object > map = new HashMap<>();
                    map . put ( "x-order" ,  RandomUtils.nextInt(lowerBound, upperBound));
                    tag .setExtensions ( map ) ;
                });
            }
            System.out.printf("Random integer generated between [%s, %s) is %s", lowerBound, upperBound, RandomUtils.nextInt(lowerBound, upperBound));
            System.out.printf("\nRandom integer generated between [0, Integer.MAX_VALUE) is %s", RandomUtils.nextInt());
            if ( openApi . getPaths ()!= null ){
                openApi . addExtension ( "x-test123" , "333" );
                openApi.getPaths ( ). addExtension ( " x-abb" , RandomUtils.nextInt(lowerBound, upperBound) ) ;
            }

        };
    }

}
