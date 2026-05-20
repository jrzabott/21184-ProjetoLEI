package pt.uab.musicaltrainer.e2e;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Ponto de entrada para todos os testes E2E via JUnit Platform Suite.
 * Configuracao em src/test/resources/junit-platform.properties.
 * Corre com: mvn test -Dtest=CucumberRunnerTest
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class CucumberRunnerTest {}
