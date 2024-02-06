package openredactle.shared.stored

import openredactle.shared.data.ArticleData
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import upickle.default.{read, write}

import java.nio.charset.Charset
import scala.language.postfixOps

class S3Storage(val bucket: String = S3Storage.bucketName):
  private lazy val s3 = S3Client.builder
    .region(Region.AP_SOUTHEAST_2)
    .build

  def fetchIndex(): Seq[(Int, String)] =
    try
      readS3Object(S3Storage.indexKey)
        .split("\n")
        .collect:
          case s"$n,$u" =>
            n.toInt -> u
    catch case _: NoSuchKeyException =>
      Nil

  def updateIndex(data: Seq[(Int, String)]): Unit =
    writeS3Object(S3Storage.indexKey, data.map((n, u) => s"$n,$u").mkString("\n"))

  def writeArticleData(index: Int, articleData: Seq[ArticleData]): Unit =
    writeS3Object(S3Storage.indexObjectPath(index), write(articleData))

  def getArticleByIndex(index: Int): Seq[ArticleData] =
    read[Seq[ArticleData]](readS3Object(S3Storage.indexObjectPath(index)))

  
  private def writeS3Object(key: String, body: String): Unit =
    s3.putObject(
      PutObjectRequest.builder
        .bucket(bucket)
        .key(key)
        .build,
      RequestBody fromString body,
    )

  private def readS3Object(key: String): String =
    s3.getObjectAsBytes(
        GetObjectRequest.builder
          .bucket(bucket)
          .key(key)
          .build,
      )
      .asString(Charset.defaultCharset())

object S3Storage:
  private val bucketName: String = "open-redactle-article-data"
  private val indexKey: String = "index"
  private val articleObjectsPrefix: String = "articles"

  private def indexObjectPath(index: Int) =
    s"${S3Storage.articleObjectsPrefix}/$index"
