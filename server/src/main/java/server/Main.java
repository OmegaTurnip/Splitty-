/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EntityScan(basePackages = { "commons", "server" })
public class Main {

    /**
     * @param args no description was provided in the template.
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    /**
     * Put some events in the database
     * @param repo The event repository
     * @return runs command line
     */
    @Bean
    public CommandLineRunner run(EventRepository repo){
        return (args -> {
            System.out.println("idem findAll can be inserted");
        });
    }

    /**
     * Delete all events from the database
     * @param repo events repository
     */
    private void deleteAllEvents(EventRepository repo){
        repo.deleteAll();
    }
}