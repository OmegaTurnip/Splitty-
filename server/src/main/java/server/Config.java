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

import java.util.Random;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.util.DebtSimplifier;
import server.financial.ExchangeRateFactory;


@Configuration
public class Config {

    /**
     * @return no description was provided in the template.
     */
    @Bean
    public Random getRandom() {
        return new Random();
    }

    /**
     * Gets the global {@link ExchangeRateFactory} instance.
     *
     * @return  The global {@code ExchangeRateFactory} instance.
     */
    @Bean
    public ExchangeRateFactory getExchangeRateFactory() {
        return new ExchangeRateFactory(ExchangeRateFactory.DEFAULT_DIR);
    }

    /**
     * Gets the global {@link DebtSimplifier} instance.
     *
     * @return  The global {@code DebtSimplifier} instance.
     */
    @Bean
    public DebtSimplifier getDebtSimplifier() {
        return new DebtSimplifier(getExchangeRateFactory());
    }
}