![alt text](https://github.com/OHDSI/Usagi/blob/master/src/org/ohdsi/usagi/ui/Usagi64.png) Usagi
===========

Introduction
========
Usagi is an application to help create mappings between coding systems and the Vocabulary standard concepts. 

Features
========
- Automatically creates an initial full mapping based on term similarity
- Uses concept names and synonyms to find potential matches
- Allows filtering the search based on target vocabulary, domain, concept class, as well as user-defined subsets of concepts
- Interactive review and correct of the initial mapping
- Export to source_to_concept_map format

Screenshot
===========
<img src="https://github.com/OHDSI/Usagi/blob/master/man/Screenshot.png" alt="Usagi" title="Usagi" />

Technology
============
Usagi is a pure Java application. It makes use of [Apache's Lucene Java library](http://lucene.apache.org/) for term matching and [Apache's POI Java libraries](http://poi.apache.org/) to read Excel files.

System Requirements
============
Requires Java 1.7 or higher. Java can be downloaded from <a href="http://www.java.com" target="_blank">http://www.java.com</a>.

Dependencies
============
 * Vocabulary version 5 (CSV files)

Getting Started
===============
1. Under the [Releases](https://github.com/OHDSI/Usagi/releases) tab, download Usagi*.zip
2. Unzip the download
3. Click on Usagi1.5GB.cmd (Windows) or Usagi1.5GB.sh (Linux, Mac) to start Usagi
4. Usagi will prompt you to specifiy the location of the Voabulary CSV files to create the index (needs to be done only once)

Getting Involved
=============
* User guide and Help: <a href="http://www.ohdsi.org/web/wiki/doku.php?id=documentation:software:Usagi">Usagi Wiki</a>
* Developer questions/comments/feedback: <a href="http://forums.ohdsi.org/c/developers">OHDSI Forum</a>
* We use the <a href="../../issues">GitHub issue tracker</a> for all bugs/issues/enhancements

License
=======
Usagi is licensed under Apache License 2.0

Development
===========
Usagi is being developed in Eclipse. Contributions are welcome.
###Development status
Beta testing
