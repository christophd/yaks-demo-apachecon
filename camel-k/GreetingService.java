/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// camel-k: language=java open-api=openapi.yaml dependency=camel-openapi-java

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

/**
 * @author Christoph Deppisch
 */
public class GreetingService extends RouteBuilder {

    private static final String APACHE_CON = "ApacheCon";

    private final Map<String, String> greetings = new HashMap<>();

    public GreetingService() {
        greetings.put(Locale.GERMAN.getLanguage(), "Hallo " + APACHE_CON + "!");
        greetings.put(Locale.ENGLISH.getLanguage(), "Hello " + APACHE_CON + "!");
        greetings.put(Locale.FRENCH.getLanguage(), "Bonjour " + APACHE_CON + "!");
        greetings.put(Locale.ITALIAN.getLanguage(), "Ciao " + APACHE_CON + "!");
        greetings.put("esp", "Hola " + APACHE_CON + "!");
    }

    @Override
    public void configure() throws Exception {
        // All endpoints starting from "direct:..." reference an operationId defined
        // in the "openapi.yaml" file.

        // Gets the openapi specification
        from("direct:openapi")
                .setBody().simple("resource:classpath:openapi.json");

        // Health check
        from("direct:health")
                .setBody().constant("{\"status\": \"UP\"}");

        // List the available languages
        from("direct:list")
                .setBody().constant(greetings.keySet())
                .marshal().json();

        // Get greeting details
        from("direct:greeting")
                .process(exchange -> {
                    String language = getLanguage(exchange);
                    String message = greetings.get(language);

                    if (message != null) {
                        exchange.getIn().setBody(String.format("{\"language\": \"%s\", \"message\": \"%s\"}", language, message));
                    } else {
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
                    }
                });

        // Create a new greeting in the in memory store
        from("direct:create")
                .convertBodyTo(String.class)
                .process(exchange -> {
                    this.greetings.put(getLanguage(exchange), exchange.getIn().getBody(String.class));
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .setBody().constant("");

        // Delete an greeting from the in memory store
        from("direct:delete")
                .process(exchange -> {
                    this.greetings.remove(getLanguage(exchange));
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
                .setBody().constant("");

        // Publish greeting event to Knative event stream
        from("direct:push-event")
                .process(exchange -> {
                    String language = getLanguage(exchange);
                    String message = Optional.ofNullable(greetings.get(language))
                            .orElseThrow(() -> new IllegalArgumentException(String.format("Given language '%s' is not available", language)));
                    exchange.getIn().setBody(String.format("{\"message\": \"%s\"}", message));
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .to("log:greeting?level=INFO");
    }

    private String getLanguage(Exchange exchange) {
        return Optional.ofNullable(exchange.getIn().getHeader("language"))
                .map(Object::toString)
                .orElseThrow(() -> new IllegalArgumentException("Missing language as request header"));
    }

}
