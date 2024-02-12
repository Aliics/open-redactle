package openredactle.server.metrics

import software.amazon.awssdk.services.cloudwatch.model.StandardUnit

case class Metric(
  name: String,
  unit: StandardUnit,
  compute: () => Double,
)

