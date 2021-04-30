package com.knu.ynortman.multitenancy.util.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SchemaOrDatabaseCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String strategy = (context.getEnvironment().getProperty("multitenancy.strategy"));
        return "database".equalsIgnoreCase(strategy) || "schema".equalsIgnoreCase(strategy);
    }

}