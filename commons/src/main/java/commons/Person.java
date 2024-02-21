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
package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;
    private String lastName;

//    @SuppressWarnings("unused")
//    private Person() {
//        // for object mapper
//    }

    /**
     * @return no description was provided in the template.
     */
    public long getId() {
        return id;
    }

    /**
     * @param id no description was provided in the template.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return no description was provided in the template.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName no description was provided in the template.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return no description was provided in the template.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName no description was provided in the template.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @param firstName no description was provided in the template.
     * @param lastName no description was provided in the template.
     */
    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * no description was provided in the template.
     */
    public Person() {

    }

    /**
     * @param obj no description was provided in the template.
     * @return no description was provided in the template.
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * @return no description was provided in the template.
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * @return no description was provided in the template.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}