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
package org.ohdsi.usagi;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.ohdsi.usagi.ui.Global;
import org.ohdsi.utilities.DirectoryUtilities;
import org.ohdsi.utilities.StringUtilities;

/**
 * The Usagi search engine is used to find matching concepts for source terms. The search engine uses Lucene.
 */
public class UsagiSearchEngine {

	public static String	MAIN_INDEX_FOLDER		= "mainIndex";
	public static String	DERIVED_INDEX_FOLDER	= "derivedIndex";
	public static String	SOURCE_CODE_TYPE_STRING	= "S";
	public static String	CONCEPT_TYPE_STRING		= "C";
	public static String	CONCEPT_TERM			= "C";
	public static String	SOURCE_TERM				= "S";

	private String			folder;
	private IndexWriter		writer;
	private IndexReader		reader					= null;
	private IndexSearcher	searcher;
	private UsagiAnalyzer	analyzer				= new UsagiAnalyzer();
	private Query			conceptQuery;
	private QueryParser		conceptIdQueryParser;
	private QueryParser		conceptClassQueryParser;
	private QueryParser		vocabularyQueryParser;
	private QueryParser		keywordsQueryParser;
	private QueryParser		standardConceptQueryParser;
	private QueryParser		domainQueryParser;
	private QueryParser		termTypeQueryParser;
	private int				numDocs;
	private FieldType		textVectorField			= getTextVectorFieldType();

	public UsagiSearchEngine(String folder) {
		this.folder = folder;
	}

	private FieldType getTextVectorFieldType() {
		FieldType textVectorField = new FieldType();
		textVectorField.setIndexed(true);
		textVectorField.setTokenized(true);
		textVectorField.setStoreTermVectors(true);
		textVectorField.setStoreTermVectorPositions(false);
		textVectorField.setStoreTermVectorPayloads(false);
		textVectorField.setStoreTermVectorOffsets(false);
		textVectorField.setStored(true);
		textVectorField.freeze();
		return textVectorField;
	}

	public void createNewMainIndex() {
		try {
			File indexFolder = new File(folder + "/" + MAIN_INDEX_FOLDER);
			if (indexFolder.exists())
				DirectoryUtilities.deleteDir(indexFolder);

			Directory dir = FSDirectory.open(indexFolder);

			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, new UsagiAnalyzer());
			iwc.setOpenMode(OpenMode.CREATE);
			iwc.setRAMBufferSizeMB(256.0);
			writer = new IndexWriter(dir, iwc);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean mainIndexExists() {
		return new File(folder + "/" + MAIN_INDEX_FOLDER).exists();
	}

	public void addTermToIndex(String term, String termType, Concept concept) {
		if (writer == null)
			throw new RuntimeException("Indexed not open for writing");
		try {
			Document document = new Document();
			document.add(new StringField("TYPE", CONCEPT_TYPE_STRING, Store.YES));
			document.add(new Field("TERM", term, textVectorField));
			document.add(new StringField("CONCEPT_ID", Integer.toString(concept.conceptId), Store.YES));
			document.add(new StringField("DOMAIN_ID", concept.domainId, Store.YES));
			document.add(new StringField("VOCABULARY_ID", concept.vocabularyId, Store.YES));
			document.add(new StringField("CONCEPT_CLASS_ID", concept.conceptClassId, Store.YES));
			document.add(new StringField("STANDARD_CONCEPT", concept.standardConcept, Store.YES));
			document.add(new StringField("TERM_TYPE", termType, Store.YES));
			writer.addDocument(document);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Tokens that appear very frequently in the source code names, but not very often in the vocabulary, would get high weights (high IDF) even though they
	 * probably are not very informative. To remedy this, we create a copy of the main index, and add all the source names to the index as well.
	 * 
	 * @param sourceCodes
	 *            the list of source codes to add to the index
	 * @param frame
	 *            a reference to the frame in case we want to show a progress dialog. Set to null if no progress dialog needs to be shown
	 */
	public void createDerivedIndex(List<SourceCode> sourceCodes, JFrame frame) {
		JDialog dialog = null;
		JProgressBar progressBar = null;
		if (frame != null) {
			dialog = new JDialog(frame, "Progress Dialog", false);

			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createRaisedBevelBorder());
			panel.setLayout(new BorderLayout());
			panel.add(BorderLayout.NORTH, new JLabel("Indexing source codes..."));
			progressBar = new JProgressBar(0, 100);
			panel.add(BorderLayout.CENTER, progressBar);
			dialog.add(panel);

			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.setSize(300, 75);
			dialog.setLocationRelativeTo(frame);
			dialog.setUndecorated(true);
			dialog.setModal(true);

		}
		AddSourceCodesThread thread = new AddSourceCodesThread(sourceCodes, progressBar, dialog);
		thread.start();
		if (dialog != null)
			dialog.setVisible(true);
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private class AddSourceCodesThread extends Thread {
		private JProgressBar		progressBar;
		private List<SourceCode>	sourceCodes;
		private JDialog				dialog;

		public AddSourceCodesThread(List<SourceCode> sourceCodes, JProgressBar progressBar, JDialog dialog) {
			this.sourceCodes = sourceCodes;
			this.progressBar = progressBar;
			this.dialog = dialog;
		}

		public void run() {
			try {
				File derivedIndexFolder = new File(folder + "/" + DERIVED_INDEX_FOLDER);
				if (derivedIndexFolder.exists())
					if (!DirectoryUtilities.deleteDir(derivedIndexFolder))
						System.out.println("Unable to delete derived index folder");

				File indexFolder = new File(folder + "/" + MAIN_INDEX_FOLDER);
				DirectoryUtilities.copyDirectory(indexFolder, derivedIndexFolder);

				Directory dir = FSDirectory.open(derivedIndexFolder);
				IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, new UsagiAnalyzer());
				iwc.setOpenMode(OpenMode.APPEND);
				iwc.setRAMBufferSizeMB(256.0);
				IndexWriter writer = new IndexWriter(dir, iwc);

				for (int i = 0; i < sourceCodes.size(); i++) {
					Document document = new Document();
					document.add(new StringField("TYPE", SOURCE_CODE_TYPE_STRING, Store.YES));
					document.add(new Field("TERM", sourceCodes.get(i).sourceName, textVectorField));
					writer.addDocument(document);
					if (progressBar != null)
						progressBar.setValue(5 + (90 * i) / sourceCodes.size());

				}
				// writer.forceMerge(1);
				writer.close();
				System.gc();
				if (dialog != null)
					dialog.setVisible(false);
				openIndexForSearching(true);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void openIndexForSearching(boolean useDerivedIndex) {
		try {
			if (useDerivedIndex)
				reader = DirectoryReader.open(FSDirectory.open(new File(folder + "/" + DERIVED_INDEX_FOLDER)));
			else
				reader = DirectoryReader.open(FSDirectory.open(new File(folder + "/" + MAIN_INDEX_FOLDER)));
			searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new DefaultSimilarity());
			BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
			QueryParser typeQueryParser = new QueryParser(Version.LUCENE_4_9, "TYPE", new KeywordAnalyzer());
			conceptQuery = typeQueryParser.parse(CONCEPT_TYPE_STRING);
			conceptIdQueryParser = new QueryParser(Version.LUCENE_4_9, "CONCEPT_ID", new KeywordAnalyzer());
			conceptClassQueryParser = new QueryParser(Version.LUCENE_4_9, "CONCEPT_CLASS_ID", new KeywordAnalyzer());
			vocabularyQueryParser = new QueryParser(Version.LUCENE_4_9, "VOCABULARY_ID", new KeywordAnalyzer());
			keywordsQueryParser = new QueryParser(Version.LUCENE_4_9, "TERM", analyzer);
			domainQueryParser = new QueryParser(Version.LUCENE_4_9, "DOMAIN_ID", new KeywordAnalyzer());
			standardConceptQueryParser = new QueryParser(Version.LUCENE_4_9, "STANDARD_CONCEPT", new KeywordAnalyzer());
			termTypeQueryParser = new QueryParser(Version.LUCENE_4_9, "TERM_TYPE", new KeywordAnalyzer());
			numDocs = reader.numDocs();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			if (reader != null) {
				reader.close();
				reader = null;
				System.gc();
			}
			if (writer != null) {
				// writer.forceMerge(1);
				writer.close();
				writer = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getTermCount() {
		return reader.numDocs();

	}

	public List<ScoredConcept> search(String searchTerm, boolean useMlt, Collection<Integer> filterConceptIds, String filterDomain, String filterConceptClass,
									  String filterVocabulary, boolean filterStandard, boolean includeSourceConcepts) {
		return search(searchTerm, useMlt, filterConceptIds, filterDomain, filterConceptClass,
				filterVocabulary, filterStandard, includeSourceConcepts, Global.dbEngine);
	}

	public List<ScoredConcept> search(String searchTerm, boolean useMlt, Collection<Integer> filterConceptIds, String filterDomain, String filterConceptClass,
			String filterVocabulary, boolean filterStandard, boolean includeSourceConcepts, BerkeleyDbEngine dbEngine) {
		List<ScoredConcept> results = new ArrayList<ScoredConcept>();
		try {
			Query query;
			if (useMlt) {
				MoreLikeThis mlt = new MoreLikeThis(searcher.getIndexReader());
				mlt.setMinTermFreq(1);
				mlt.setMinDocFreq(1);
				mlt.setMaxDocFreq(9999);
				mlt.setMinWordLen(1);
				mlt.setMaxWordLen(9999);
				mlt.setMaxDocFreqPct(100);
				mlt.setMaxNumTokensParsed(9999);
				mlt.setMaxQueryTerms(9999);
				mlt.setStopWords(null);
				mlt.setFieldNames(new String[] { "TERM" });
				mlt.setAnalyzer(analyzer);

				query = mlt.like("TERM", new StringReader(searchTerm));
			} else {
				try {
					query = keywordsQueryParser.parse(searchTerm);
				} catch (ParseException e) {
					return results;
				}
			}

			BooleanQuery booleanQuery = new BooleanQuery();
			booleanQuery.add(query, Occur.SHOULD);
			booleanQuery.add(conceptQuery, Occur.MUST);

			if (filterConceptIds != null && filterConceptIds.size() > 0) {
				Query conceptIdQuery = conceptIdQueryParser.parse(StringUtilities.join(filterConceptIds, " OR "));
				booleanQuery.add(conceptIdQuery, Occur.MUST);
			}

			if (filterDomain != null) {
				Query domainQuery = domainQueryParser.parse("\"" + filterDomain + "\"");
				booleanQuery.add(domainQuery, Occur.MUST);
			}
			if (filterConceptClass != null) {
				Query conceptClassQuery = conceptClassQueryParser.parse("\"" + filterConceptClass.toString() + "\"");
				booleanQuery.add(conceptClassQuery, Occur.MUST);
			}
			if (filterVocabulary != null) {
				Query vocabularyQuery = vocabularyQueryParser.parse("\"" + filterVocabulary.toString() + "\"");
				booleanQuery.add(vocabularyQuery, Occur.MUST);
			}
			if (filterStandard) {
				Query standardQuery = standardConceptQueryParser.parse("S");
				booleanQuery.add(standardQuery, Occur.MUST);
			}
			if (!includeSourceConcepts) {
				Query termTypeQuery = termTypeQueryParser.parse(CONCEPT_TERM);
				booleanQuery.add(termTypeQuery, Occur.MUST);
			}
			TopDocs topDocs = searcher.search(booleanQuery, 100);

			recomputeScores(topDocs.scoreDocs, query);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document document = reader.document(scoreDoc.doc);
				int conceptId = Integer.parseInt(document.get("CONCEPT_ID"));
				Concept targetConcept = dbEngine.getConcept(conceptId);
				String term = document.get("TERM");
				// If matchscore = 0 but it was the one concept that was automatically selected, still allow it:
				if (scoreDoc.score > 0 || (filterConceptIds != null && filterConceptIds.size() == 1 && filterConceptIds.contains(targetConcept.conceptId)))
					results.add(new ScoredConcept(scoreDoc.score, term, targetConcept));
			}
			reorderTies(results);
			removeDuplicateConcepts(results);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		return results;
	}

	private void removeDuplicateConcepts(List<ScoredConcept> results) {
		Set<Integer> seenConceptIds = new HashSet<Integer>();
		Iterator<ScoredConcept> iterator = results.iterator();
		while (iterator.hasNext()) {
			ScoredConcept scoredConcept = iterator.next();
			if (!seenConceptIds.add(scoredConcept.concept.conceptId))
				iterator.remove();
		}
	}

	private void reorderTies(List<ScoredConcept> scoredConcepts) {
		Collections.sort(scoredConcepts, new Comparator<ScoredConcept>() {

			@Override
			public int compare(ScoredConcept arg0, ScoredConcept arg1) {
				int result = -Float.compare(arg0.matchScore, arg1.matchScore);
				if (result == 0) {
					if (arg0.term.toLowerCase().equals(arg0.concept.conceptName.toLowerCase()))
						return -1;
					else if (arg1.term.toLowerCase().equals(arg1.concept.conceptName.toLowerCase()))
						return 1;
				}
				return result;
			}
		});
	}

	/**
	 * Lucene's matching score does some weird things: it is not normalized (the value can be greater than 1), and not all tokens are included in the
	 * computation. For that reason, we're recomputing the matching score as plain TF*IDF cosine matching here.
	 * 
	 * @param scoreDocs
	 *            The array of documents scored by Lucene
	 * @param query
	 *            The query used for retrieval
	 */
	private void recomputeScores(ScoreDoc[] scoreDocs, Query query) {
		try {
			Term2Tfidf searchTerm = null;
			if (query instanceof BooleanQuery)
				searchTerm = new Term2Tfidf((BooleanQuery) query);
			else if (query instanceof TermQuery)
				searchTerm = new Term2Tfidf((TermQuery) query);

			if (searchTerm != null && !searchTerm.isInvalid()) {
				for (ScoreDoc scoreDoc : scoreDocs) {
					Term2Tfidf hit = new Term2Tfidf(scoreDoc.doc, "TERM");
					scoreDoc.score = (float) searchTerm.cosineSimilarity(hit);
				}
				Arrays.sort(scoreDocs, new Comparator<ScoreDoc>() {

					@Override
					public int compare(ScoreDoc arg0, ScoreDoc arg1) {
						return -Float.compare(arg0.score, arg1.score);
					}
				});
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static class ScoredConcept {
		public float	matchScore;
		public Concept	concept;
		public String	term;

		public ScoredConcept(float matchScore, String term, Concept concept) {
			this.matchScore = matchScore;
			this.term = term;
			this.concept = concept;
		}
	}

	private class Term2Tfidf {
		public TermTfidfPair[]	pairs;
		public double			l1;
		public boolean			invalid	= false;

		public Term2Tfidf(int docId, String field) throws IOException {
			Terms vector = reader.getTermVector(docId, field);

			pairs = new TermTfidfPair[(int) vector.size()];
			l1 = 0;

			TermsEnum termsEnum = vector.iterator(null);
			int i = 0;
			BytesRef text;
			while ((text = termsEnum.next()) != null) {
				double tfidf = termsEnum.totalTermFreq() * idf(reader.docFreq(new Term(field, termsEnum.term())), numDocs);
				pairs[i++] = new TermTfidfPair(BytesRef.deepCopyOf(text), tfidf);
				l1 += sqr(tfidf);
			}

			l1 = Math.sqrt(l1);
			sort();
		}

		public boolean isInvalid() {
			return invalid;
		}

		public Term2Tfidf(BooleanQuery query) throws IOException {
			pairs = new TermTfidfPair[query.clauses().size()];
			l1 = 0;
			int i = 0;
			for (BooleanClause clause : query.clauses()) {
				if (!(clause.getQuery() instanceof TermQuery))
					invalid = true;
				else {
					TermQuery q = (TermQuery) clause.getQuery();
					double tfidf = idf(reader.docFreq(q.getTerm()), numDocs);
					pairs[i++] = new TermTfidfPair(q.getTerm().bytes(), tfidf);
					l1 += sqr(tfidf);
				}
			}
			if (!invalid) {
				l1 = Math.sqrt(l1);
				sort();
			}
		}

		public Term2Tfidf(TermQuery query) throws IOException {
			pairs = new TermTfidfPair[1];
			l1 = 0;
			int i = 0;
			double tfidf = idf(reader.docFreq(query.getTerm()), numDocs);
			pairs[i++] = new TermTfidfPair(query.getTerm().bytes(), tfidf);
			l1 += sqr(tfidf);
			l1 = Math.sqrt(l1);
			sort();
		}

		public void sort() {
			Arrays.sort(pairs, new Comparator<TermTfidfPair>() {

				@Override
				public int compare(TermTfidfPair o1, TermTfidfPair o2) {
					return o1.term.compareTo(o2.term);
				}
			});
		}

		public double cosineSimilarity(Term2Tfidf other) {
			int cursor1 = 0;
			int cursor2 = 0;
			double dotProduct = 0;
			while (cursor1 < pairs.length && cursor2 < other.pairs.length) {
				int compare = pairs[cursor1].term.compareTo(other.pairs[cursor2].term);
				if (compare == 0) {
					dotProduct += pairs[cursor1].tfidf * other.pairs[cursor2].tfidf;
					cursor1++;
					cursor2++;
				} else if (compare < 0) {
					cursor1++;
				} else {
					cursor2++;
				}
			}
			if (l1 == 0 || other.l1 == 0)
				return 0;
			else
				return dotProduct / (l1 * other.l1);
		}
	}

	private class TermTfidfPair {
		public TermTfidfPair(BytesRef term, Double tfidf) {
			this.term = term;
			this.tfidf = tfidf;
		}

		public BytesRef	term;
		public double	tfidf;
	}

	private double sqr(double x) {
		return x * x;
	}

	private double idf(int docFreq, int d) {
		return Math.log(d / (double) docFreq);
	}

	public boolean isOpenForSearching() {
		return (reader != null);
	}
}
