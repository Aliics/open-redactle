package openredactle.shared.stored

import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*

import java.nio.charset.Charset
import scala.language.postfixOps

class S3Storage(val bucket: String = S3Storage.bucketName):
  private lazy val s3 = S3Client.builder
    .region(Region.AP_SOUTHEAST_2)
    .build

  def fetchIndex(): Seq[(Int, String)] =
    try
      s3.getObjectAsBytes(
          GetObjectRequest.builder
            .bucket(bucket)
            .key(S3Storage.indexKey)
            .build,
        )
        .asString(Charset.defaultCharset)
        .split("\n")
        .collect:
          case s"$n,$u" =>
            n.toInt -> u
    catch case _: NoSuchKeyException =>
      Nil

  def updateIndex(data: Seq[(Int, String)]): Unit =
    s3.putObject(
      PutObjectRequest.builder
        .bucket(bucket)
        .key(S3Storage.indexKey)
        .build,
      RequestBody.fromString(data.map((n, u) => s"$n,$u").mkString("\n"))
    )

object S3Storage:
  val bucketName: String = "open-redactle-article-data"
  val indexKey: String = "index"

