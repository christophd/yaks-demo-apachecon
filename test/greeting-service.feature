Feature: Greeting Service

  Background:
    Given URL: http://greeting-service-demo-apachecon.apps.cdeppisc.rhmw-integrations.net

  Scenario: Health check
    Given wait for GET on path /health to return 200 OK

  Scenario: List languages
    When send GET
    Then verify HTTP response body
    """
    ["de","esp","en","it","fr"]
    """
    Then receive HTTP 200 OK

  Scenario: Add greeting
    Given HTTP request body: Namaste ApacheCon!
    When send PUT /ind
    Then receive HTTP 201 CREATED

  Scenario: Get greeting
    When send GET /ind
    Then verify HTTP response body: {"language": "ind", "message": "Namaste ApacheCon!"}
    And receive HTTP 200 OK

  Scenario: Delete greeting
    When send DELETE /ind
    And receive HTTP 204 NO_CONTENT

  Scenario: Greeting not found
    When send GET /xxx
    And receive HTTP 404 NOT_FOUND
