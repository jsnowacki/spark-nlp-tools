package com.sigdelta.spark.nlp.tools

import com.optimaize.langdetect
import com.optimaize.langdetect.LanguageDetectorBuilder
import com.optimaize.langdetect.ngram.NgramExtractors
import com.optimaize.langdetect.profiles.LanguageProfileReader
import com.optimaize.langdetect.text.{CommonTextObjectFactories, TextObjectFactory}


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
  def apply(): LanguageDetector = new LanguageDetector()
}