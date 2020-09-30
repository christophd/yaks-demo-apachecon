Feature: Greetings

  Background:
    Given URL: http://greeting-service-demo-apachecon.apps.cdeppisc.rhmw-integrations.net

  Scenario Outline: Get greetings
    When send GET /<lang>
    Then verify HTTP response body: {"language": "<lang>", "message": "<greeting>"}
    And receive HTTP 200 OK

  Examples:
  | lang  | greeting         |
  | en    | Hello ApacheCon! |
  | it    | Ciao ApacheCon!  |
  | esp   | Hola ApacheCon!  |
