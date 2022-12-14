package nz.co.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.*;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
@EnableOpenApi
public class SwaggerConfiguration{
    @Bean
    public Docket webApiDoc(){
        return new Docket(DocumentationType.OAS_30)
                .groupName("User API document")
                .pathMapping("/")
                .enable(true)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("nz.co"))
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .globalRequestParameters(globalRequestParemeters())
                .globalResponses(HttpMethod.GET,globalResponses())
                .globalResponses(HttpMethod.POST,globalResponses());
    }

    @Bean
    public Docket adminApiDoc(){
        return new Docket(DocumentationType.OAS_30)
                .groupName("Admin API document")
                .pathMapping("/")
                .enable(true)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("nz.co"))
                .paths(PathSelectors.ant("/admin/**"))
                .build();
    }

    private List<RequestParameter> globalRequestParemeters() {
        List<RequestParameter> listRequestParameter = new ArrayList<RequestParameter>();
        listRequestParameter.add(new RequestParameterBuilder()
                .name("token")
                .description("Login Token")
                .in(ParameterType.HEADER)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(false)
                .build());
        return listRequestParameter;
    }

    private List<Response> globalResponses() {
        List<Response> listResponse = new ArrayList<Response>();
        listResponse.add(new ResponseBuilder()
                .code("4xx")
                .description("Request error,please check through error code and error message").build());
        return listResponse;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Amazing online class")
                .description("Micro service API document")
                .contact(new Contact("Bruce Zhou", "http://localhost:9090", "18911717812@189.cn"))
                .version("1.0")
                .build();
    }
}