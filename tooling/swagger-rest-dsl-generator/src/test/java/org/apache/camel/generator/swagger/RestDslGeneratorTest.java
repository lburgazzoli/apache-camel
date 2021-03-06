/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
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
package org.apache.camel.generator.swagger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestsDefinition;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RestDslGeneratorTest {

    final Swagger swagger = new SwaggerParser().read("petstore.json");

    @Test
    public void shouldCreateDefinitions() {
        final CamelContext context = new DefaultCamelContext();

        final RestsDefinition definition = RestDslGenerator.toDefinition(swagger).generate(context);

        assertThat(definition).isNotNull();
    }

    @Test
    public void shouldGenerateSourceCodeWithDefaults() throws IOException, URISyntaxException {
        final StringBuilder code = new StringBuilder();

        RestDslGenerator.toAppendable(swagger).generate(code);

        final URI file = RestDslGeneratorTest.class.getResource("/SwaggerPetstore.txt").toURI();
        final String expectedContent = new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);

        assertThat(code.toString()).isEqualTo(expectedContent);
    }

    @Test
    public void shouldGenerateSourceCodeWithOptions() throws IOException, URISyntaxException {
        final StringBuilder code = new StringBuilder();

        RestDslGenerator.toAppendable(swagger).withClassName("MyRestRoute").withPackageName("com.example")
            .withIndent("\t").withDestinationGenerator(o -> "direct:rest-" + o.getOperationId()).generate(code);

        final URI file = RestDslGeneratorTest.class.getResource("/MyRestRoute.txt").toURI();
        final String expectedContent = new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);

        assertThat(code.toString()).isEqualTo(expectedContent);
    }
}
