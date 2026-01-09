# HTTP Client

## Setup

Create a file called `http-client.private.env.json` like this:

```json
{
  "local": {
    "client-id": "<<ui-client-id>>",
    "client-secret": "<<ui-client-secret>>"
  }
}
```

where `<<ui-client-id>>` and `<<ui-client-secret>>` can be obtained from Kubernetes secrets.

See IntelliJ HTTP client docs [here](https://www.jetbrains.com/help/idea/http-client-variables.html#example-working-with-environment-files).