# YAKS Demo ApacheCon

YAKS demo for ApacheCon @Home 2020

# Setup

The tests use Camel K integrations as SUT (System Under Test). We need to start the Camel K integrations first before running any test.

You can run the OpenAPI greeting service and the splitter EIP as follows:

```shell script
$ kamel run camel-k/GreetingService.java --resource camel-k/openapi.json
$ kamel run camel-k/greeting-splitter.groovy
```                             

This runs the greeting service and exposes the REST API. Next step is to find out how the test can access the service within the cluster.

Adjust the URL of the service in the feature files accordingly.

We can verify the service now by running:

```shell script
$ yaks test test/greeting-service.feature
```
