/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sigdelta.spark.nlp.jobs

import com.sigdelta.spark.nlp.tools.{LanguageDetectorObject, LanguageDetector}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object LangDetectUdfUsage {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("LangDetectUdfTest")
      .getOrCreate()
    import spark.implicits._

    val languageDetector = LanguageDetector()

    val sampleText = "Now what is that?"
    println(sampleText, languageDetector.detect(sampleText))

    val df = List(
      "To będzie po polsku.",
      "And this is in English.",
      "Und das ist auf Deutsch.").toDF("text")

    df.show()

    // Creating UDF with a broadcasted class
    val ldBcast = spark.sparkContext.broadcast(languageDetector)
    val ldUdf = udf((text: String) => ldBcast.value.detect(text))
    df.select('text, ldUdf('text).as('lang)).show()

    // you can also use companion object directly
    val ldCompanionUdf = udf((text: String) => LanguageDetector.detect(text))
    df.select('text, ldCompanionUdf('text).as('lang)).show()

    // alternatively you can use full object implementation (just lazy without transient)
    val ldAsObjectUdf = udf((text: String) => LanguageDetectorObject.detect(text))
    df.select('text, ldAsObjectUdf('text).as('lang)).show()
  }
}
