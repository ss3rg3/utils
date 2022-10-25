/*
 * Copyright 2022 Sergej Schaefer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils;

import java.util.function.Predicate;

/**
 * Builder Util for validating and setting defaults from builders in a single place
 */
public class Butil<T> {

    private final T object;

    private Butil(T object) {
        this.object = object;
    }

    private Butil(T object, T defaultValue) {
        if (object == null) {
            object = defaultValue;
        }
        this.object = object;
    }

    /**
     * Same as mandatoryValue() but more descriptive, if you just need an assertion.
     */
    public static <T> Butil<T> assertThat(T object) {
        return new Butil<>(object);
    }

    /**
     * Value without a default. Note that there is NO implicit validation, i.e. you need to use isNotNull() additionally
     */
    public static <T> Butil<T> mandatoryValue(T object) {
        return new Butil<>(object);
    }

    /**
     * Value with a default, which makes it optional. Default is used if provided 'object' is NULL. Default value has
     * to pass all validations.
     */
    public static <T> Butil<T> optionalValue(T object, T defaultValue) {
        return new Butil<>(object, defaultValue);
    }

    public Butil<T> isNotNull(String errorMessage) {
        if (this.object == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        return this;
    }

    public Butil<T> isTrue(Boolean condition, String errorMessage) {
        if (!condition) {
            throw new IllegalArgumentException(errorMessage);
        }
        return this;
    }

    public Butil<T> isTrue(Predicate<T> predicate, String errorMessage) {
        if (!predicate.test(this.object)) {
            throw new IllegalArgumentException(errorMessage);
        }
        return this;
    }

    public Butil<T> isFalse(Boolean condition, String errorMessage) {
        if (condition) {
            throw new IllegalArgumentException(errorMessage);
        }
        return this;
    }

    public Butil<T> isFalse(Predicate<T> predicate, String errorMessage) {
        if (predicate.test(this.object)) {
            throw new IllegalArgumentException(errorMessage);
        }
        return this;
    }

    public T get() {
        return this.object;
    }

}
