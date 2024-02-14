package openredactle.server.metrics

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.cloudwatch.model.{MetricDatum, PutMetricDataRequest}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.FutureConverters.*

class CloudWatchEmitter(namespace: String)(val metrics: Metric*)(using ExecutionContext):
  private val cloudwatch = CloudWatchAsyncClient.builder
    .region(Region.AP_SOUTHEAST_2)
    .build

  emit()
  private def emit(): Future[Unit] =
    for
      _ <- Future.traverse(metrics):
        case Metric(name, unit, compute) =>
          cloudwatch.putMetricData(
            PutMetricDataRequest.builder
              .namespace(namespace)
              .metricData(
                MetricDatum.builder
                  .metricName(name)
                  .unit(unit)
                  .value(compute())
                  .build
              )
              .build
          ).asScala

      _ <- Future(Thread.sleep(10_000))
      _ <- emit()
    yield ()
