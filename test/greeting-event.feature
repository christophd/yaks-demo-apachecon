Feature: Greeting Event

  Background:
    Given URL: http://greeting-service-demo-apachecon.apps.cdeppisc.rhmw-integrations.net
    Given Kafka connection
      | url   | demo-kafka-cluster-kafka-bootstrap:9092 |
      | topic | greetings                               |

  Scenario: Push greeting event
    When send POST /event/en
    Then receive HTTP 201 CREATED
    Then expect message in Kafka with body: {"message": "Hello ApacheCon!"}
