package openredactle.server.metrics

import openredactle.server.Env
import openredactle.shared.logging.ImplicitLazyLogger
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.cloudwatch.model.{MetricDatum, PutMetricDataRequest}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.FutureConverters.*

class CloudWatchEmitter(namespace: String)(val metrics: Metric*)
  (using env: Env, ec: ExecutionContext) extends ImplicitLazyLogger:

  private val cloudwatch = CloudWatchAsyncClient.builder
    .region(Region.AP_SOUTHEAST_2)
    .build

  emit()
  private def emit(): Future[Unit] =
    for
      _ <- Future.traverse(metrics):
        case Metric(name, unit, compute) =>
          val fullNamespace = s"${env.metricsPrefix}/$namespace"

          if env.emitMetrics then
            cloudwatch.putMetricData(
              PutMetricDataRequest.builder
                .namespace(fullNamespace)
                .metricData(
                  MetricDatum.builder
                    .metricName(name)
                    .unit(unit)
                    .value(compute())
                    .build
                )
                .build
            ).asScala
          else Future:
            logger.info(s"$fullNamespace/$name ($unit) => ${compute()}")

      _ <- Future(Thread.sleep(10_000))
      _ <- emit()
    yield ()
