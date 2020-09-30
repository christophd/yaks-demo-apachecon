Feature: Greeting Service

  Background:
    Given OpenAPI specification: http://greeting-service-demo-apachecon.apps.cdeppisc.rhmw-integrations.net/openapi

  Scenario: Health check
    When invoke operation: health
    Then verify operation result: 200 OK
