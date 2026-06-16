package com.fiap.restaurant_management_v2.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.fiap.restaurant_management_v2")
class CleanArchitectureTest {
    @ArchTest
    static final ArchRule layers_respect_dependency_rule = layeredArchitecture()
        .consideringOnlyDependenciesInLayers()
        .withOptionalLayers(true)
        .layer("Domain").definedBy("..restaurant_management_v2.domain..")
        .layer("Application").definedBy("..restaurant_management_v2.application..")
        .layer("InterfaceAdapters").definedBy("..restaurant_management_v2.adapters..")
        .layer("Infrastructure").definedBy("..restaurant_management_v2.infrastructure..")
        .whereLayer("Domain").mayNotAccessAnyLayer()
        .whereLayer("Application").mayOnlyAccessLayers("Domain")
        .whereLayer("InterfaceAdapters").mayOnlyAccessLayers("Domain", "Application")
        .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();

    @ArchTest
    static final ArchRule inner_circles_are_framework_free = noClasses()
        .that().resideInAnyPackage(
            "..restaurant_management_v2.domain..",
            "..restaurant_management_v2.application..",
            "..restaurant_management_v2.adapters..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "org.springframework..",
            "jakarta..",
            "org.hibernate..",
            "com.fasterxml.jackson..",
            "tools.jackson..")
        .as("circles 1-3 (domain, application, interface adapters) must not depend on any framework")
        .allowEmptyShould(true);
}
