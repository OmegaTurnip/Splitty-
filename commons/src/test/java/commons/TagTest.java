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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TagTest {


    @Test
    void testEqual(){
        Tag food = new Tag("food", "blue");
        food.setId(1L);
        Tag food2 = new Tag("food", "blue");
        food2.setId(1L);
        assertTrue(food.equals(food2));
    }

    @Test
    void testNotEqual(){
        Tag food = new Tag("food", "blue");
        food.setId(1L);
        Tag movies = new Tag("movies", "red");
        food.setId(1L);
        assertFalse(food.equals(movies));
    }

    @Test
    void testNameEquals(){
        Tag food = new Tag("food", "blue");
        Tag food2 = new Tag("food", "red");
        assertTrue(food.nameEquals(food2));
    }

}
