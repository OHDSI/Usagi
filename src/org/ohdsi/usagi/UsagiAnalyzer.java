/*******************************************************************************
 * Copyright 2017 Observational Health Data Sciences and Informatics
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.ohdsi.usagi;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.util.Version;

/**
 * Analyzers are used by Lucene to turn a piece of text into a list of tokens. For Usagi we use a custom analyzer that things like split words on letter-number
 * boundaries (e.g. '10mg' is converted into two tokens: '10' and 'mg'). Other transformations include converting to lower case, and stemming words (e.g.
 * converting plural to singular form).
 */
public class UsagiAnalyzer extends Analyzer {

	private static Version	matchVersion	= Version.LUCENE_4_9;

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		final Tokenizer source = new NGramTokenizer(matchVersion, reader, 2, 3);
		TokenStream result = new StandardFilter(matchVersion, source);
		result = new LowerCaseFilter(matchVersion, result);
		return new TokenStreamComponents(source, result);

		// final Tokenizer source = new StandardTokenizer(matchVersion, reader);
		// TokenStream result = new StandardFilter(matchVersion, source);
		//
		// result = new EnglishPossessiveFilter(matchVersion, result);
		// result = new LowerCaseFilter(matchVersion, result);
		// result = new PorterStemFilter(result);
		// result = new WordDelimiterFilter(matchVersion, result, WordDelimiterFilter.ALPHANUM | WordDelimiterFilter.SUBWORD_DELIM
		// | WordDelimiterFilter.SPLIT_ON_NUMERICS, null);
		// return new TokenStreamComponents(source, result);
	}

}
