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

package com.sigdelta.spark.nlp.tools

import com.optimaize.langdetect
import com.optimaize.langdetect.LanguageDetectorBuilder
import com.optimaize.langdetect.ngram.NgramExtractors
import com.optimaize.langdetect.profiles.LanguageProfileReader
import com.optimaize.langdetect.text.{CommonTextObjectFactories, TextObjectFactory}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf

class LanguageDetector extends Serializable {

  @transient lazy val languageDetector: langdetect.LanguageDetector = {
    val languageProfiles = new LanguageProfileReader().readAllBuiltIn
    LanguageDetectorBuilder.create(NgramExtractors.standard).withProfiles(languageProfiles).build
  }

  @transient lazy val textObjectFactory: TextObjectFactory = CommonTextObjectFactories.forDetectingShortCleanText

  def detect(text: String): Option[String] = {
    val textObject = textObjectFactory.forText(text)
    val lang = languageDetector.detect(textObject)
    if (lang.isPresent)
      Some(lang.get.getLanguage)
    else
      None
  }

}

object LanguageDetector {

  private lazy val languageDetector: LanguageDetector = new LanguageDetector()

  def apply(): LanguageDetector = languageDetector

  def detect(text: String): Option[String] = languageDetector.detect(text)

  def getUdf: UserDefinedFunction = udf((text: String) => detect(text))

  def registerUdf(alias: String): UserDefinedFunction = {
    val spark = SparkSession.builder().getOrCreate()

    spark.udf.register(alias, (text: String) => detect(text))
  }

  // Alias can be done with default, but this way plays better with Py4j
  def registerUdf: UserDefinedFunction = registerUdf("lang")

}