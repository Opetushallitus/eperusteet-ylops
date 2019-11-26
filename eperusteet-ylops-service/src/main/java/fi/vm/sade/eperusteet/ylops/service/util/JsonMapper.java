/*
 *  Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 *  This program is free software: Licensed under the EUPL, Version 1.1 or - as
 *  soon as they will be approved by the European Commission - subsequent versions
 *  of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 */

package fi.vm.sade.eperusteet.ylops.service.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.converter.GenericHttpMessageConverter;

import java.io.*;
import java.util.Optional;

/**
 * User: tommiratamaa
 * Date: 16.11.2015
 * Time: 15.28
 */
public interface JsonMapper {
    JsonNode readTree(byte[] bytes) throws IOException;

    <T> byte[] writeValueAsBytes(T data) throws IOException;

    default <T> T deserialize(Class<T> t, String from) throws IOException {
        return deserialize(t, new StringReader(from));
    }

    <T> T deserialize(Class<T> t, Reader from) throws IOException;

    default <T> String serialize(T obj) throws IOException {
        StringWriter writer = new StringWriter();
        serialize(obj, writer);
        return writer.getBuffer().toString();
    }

    <T> JsonNode toJson(T obj) throws IOException;

    <T> JsonNode readTree(String str) throws IOException;

    <T> void serialize(T obj, Writer to) throws IOException;

    default Optional<? extends GenericHttpMessageConverter<?>> messageConverter() {
        return Optional.empty();
    }

    default <T> Optional<T> unwrap(Class<T> clzz) {
        return Optional.empty();
    }
}
