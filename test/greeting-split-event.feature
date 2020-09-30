@require(com.consol.citrus:citrus-validation-hamcrest:@citrus.version@)
Feature: Split greeting

  Background:
    Given URL: http://greeting-service-demo-apachecon.apps.cdeppisc.rhmw-integrations.net
    Given Kafka connection
      | url   | demo-kafka-cluster-kafka-bootstrap:9092 |
      | topic | words                                   |

  Scenario: Split greeting event
    When send POST /event/de
    Then receive HTTP 201 CREATED
    Then expect message in Kafka with body: @assertThat(anyOf(is(Hallo), is(ApacheCon!)))@
    And expect message in Kafka with body: @assertThat(anyOf(is(Hallo), is(ApacheCon!)))@
