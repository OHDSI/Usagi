/*******************************************************************************
 * Copyright 2018 Observational Health Data Sciences and Informatics
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
package org.ohdsi.usagi.tests;
import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.ohdsi.utilities.DirectoryUtilities;

/**
 * This test explores some weird behaviour of Lucene. It shows the reason why we're currently recomputing the matching score using
 * simple TF*IDF cosine matching.
 * @author MSCHUEMI
 *
 */
public class TestLucene {
	public static String	folder	= "c:/temp/index";

	public static void main(String[] args) throws IOException, ParseException {
		 Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		//Analyzer analyzer = new UsagiAnalyzer();
		FieldType textVectorField = new FieldType();
		textVectorField.setIndexed(true);
		textVectorField.setTokenized(true);
		textVectorField.setStoreTermVectors(true);
		textVectorField.setStoreTermVectorPositions(false);
		textVectorField.setStoreTermVectorPayloads(false);
		textVectorField.setStoreTermVectorOffsets(false);
		textVectorField.setStored(true);
		textVectorField.freeze();

		File indexFolder = new File(folder);
		if (indexFolder.exists())
			DirectoryUtilities.deleteDir(indexFolder);

		Directory dir = FSDirectory.open(indexFolder);

		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		iwc.setRAMBufferSizeMB(256.0);
		IndexWriter writer = new IndexWriter(dir, iwc);
		Document doc = new Document();
		doc.add(new Field("F", "word1 word2 w3 word4", textVectorField));
		writer.addDocument(doc);
		doc = new Document();
		doc.add(new Field("F", "word1 word2 w3", textVectorField));
		writer.addDocument(doc);

		writer.close();

		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(folder)));
		for (int i = 0; i < reader.numDocs(); i++) {
			TermsEnum termsEnum = reader.getTermVector(i, "F").iterator(null);
			BytesRef text;
			while ((text = termsEnum.next()) != null) {
				System.out.print(text.utf8ToString() + ",");
			}
			System.out.println();
		}
		IndexSearcher searcher = new IndexSearcher(reader);

		// MoreLikeThis mlt = new MoreLikeThis(searcher.getIndexReader());
		// mlt.setMinTermFreq(0);
		// mlt.setMinDocFreq(0);
		// mlt.setMaxDocFreq(9999);
		// mlt.setMinWordLen(0);
		// mlt.setMaxWordLen(9999);
		// mlt.setMaxDocFreqPct(100);
		// mlt.setMaxNumTokensParsed(9999);
		// mlt.setMaxQueryTerms(9999);
		// mlt.setStopWords(null);
		// mlt.setFieldNames(new String[] { "F" });
		// mlt.setAnalyzer(new UsagiAnalyzer());
		// Query query = mlt.like("F", new StringReader("Systolic blood pressure"));
		QueryParser parser = new QueryParser(Version.LUCENE_4_9, "F", analyzer);
		Query query = parser.parse("word1");
		
		Explanation explanation = searcher.explain(query, 0);
		print(explanation);
		System.out.println();
		explanation = searcher.explain(query, 1);
		print(explanation);
		System.out.println();
		
		TopDocs topDocs = searcher.search(query, 99);
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			System.out.println(scoreDoc.score + "\t" + reader.document(scoreDoc.doc).get("F"));
		}
	}

	private static void print(Explanation explanation) {
		System.out.println(explanation.getDescription() + "value:"+explanation.getValue());
		if (explanation.getDetails() != null)
		for (Explanation explanation2 : explanation.getDetails())
			print(explanation2);
		
	}
}
