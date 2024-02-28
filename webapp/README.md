# webapp

Scala.js frontend written using Laminar.

# Deployment

This is served using an S3 static object bucket. We deploy it with the ops script [deploy-webapp](../ops/deploy-webapp).
That script handles the full flow, including invalidation in CloudFront.
