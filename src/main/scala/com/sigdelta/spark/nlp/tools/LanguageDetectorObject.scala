/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

/**
  * Alternative solution to LanguageDetector
  */
object LanguageDetectorObject {
  lazy val languageDetector: langdetect.LanguageDetector = {
    val languageProfiles = new LanguageProfileReader().readAllBuiltIn
    LanguageDetectorBuilder.create(NgramExtractors.standard).withProfiles(languageProfiles).build
  }

  lazy val textObjectFactory: TextObjectFactory = CommonTextObjectFactories.forDetectingShortCleanText

  def detect(text: String): Option[String] = {
    val textObject = textObjectFactory.forText(text)
    val lang = languageDetector.detect(textObject)
    if (lang.isPresent)
      Some(lang.get.getLanguage)
    else
      None
  }
}
