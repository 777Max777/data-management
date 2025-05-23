package com.lma.core.task.api;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.OffsetDateTime;

@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BigInteger.class, name = "BigInteger"),
        @JsonSubTypes.Type(value = OffsetDateTime.class, name = "Date"),
        @JsonSubTypes.Type(value = BigDecimal.class, name = "BigDecimal"),
        @JsonSubTypes.Type(value = String.class, name = "String")
})
public @interface SupportTypes {
}
