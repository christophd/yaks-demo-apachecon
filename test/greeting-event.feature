Feature: Greeting Event

  Background:
    Given URL: http://greeting-service-demo-apachecon.apps.cdeppisc.rhmw-integrations.net

  Scenario: Push greeting event
    When send POST /event/en
    Then receive HTTP 201 CREATED
