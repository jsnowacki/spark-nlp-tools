package com.sigdelta.spark.nlp.jobs

import com.sigdelta.spark.nlp.tools.LanguageDetector
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf

object LangDetectUdfUsage {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("LangDetectUdfTest")
      .getOrCreate()
    import spark.implicits._

    val languageDetector = LanguageDetector()

    val sampleText = "Now what is that?"
    println(sampleText, languageDetector.detect(sampleText))

    val ldBcast = spark.sparkContext.broadcast(languageDetector)

    val ldUdf = udf((text: String) => ldBcast.value.detect(text))

    val df = List(
      "To będzie po polsku.",
      "And this is in English.",
      "Und das ist auf Deutsch.").toDF("text")

    df.show()

    df.select('text, ldUdf('text).as('lang)).show()

  }
}
