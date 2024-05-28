![alt text](https://github.com/OHDSI/Usagi/blob/master/src/org/ohdsi/usagi/ui/Usagi64.png) Usagi
===========

Introduction
========
Usagi is an application to help create mappings between coding systems and the Vocabulary standard concepts. 

Features
========
- Automatically creates an initial full mapping based on term similarity.
- Uses concept names and synonyms to find potential matches.
- Optionally use names and synonyms of source concepts to find potential matches.
- Allows filtering the search results based on target vocabulary, domain, concept class, as well as user-defined subsets of concepts.
- Interactive review and correction of the initial mapping.
- Inspect information about target concepts, such as their parents, children, and source concepts.
- Export to source_to_concept_map format.
- Apply an old mapping to an updated set of source codes. Useful for when the vocab or the set of source codes has updated (or both).

Screenshot
===========
<img src="https://github.com/OHDSI/Usagi/blob/master/docs/images/Screenshot.png" alt="Usagi" title="Usagi" />

Technology
============
Usagi is a pure Java application. It makes use of [Apache's Lucene Java library](http://lucene.apache.org/) for term matching, [Apache's POI Java libraries](http://poi.apache.org/) to read Excel files, and [Oracle's Berkeley DB](http://www.oracle.com/technetwork/database/database-technologies/berkeleydb/overview/index.html) to store concept information.

Intended Use
============
Usagi was designed and implemented for use within a secure and trusted environment. No efforts have been made to encrypt or otherwise protect the passwords, parameters and results. This should be kept in mind when deploying this tool.

System Requirements
============
Requires Java 1.8 or higher. Java can be downloaded from <a href="http://www.java.com" target="_blank">http://www.java.com</a>.

Dependencies
============
 * Vocabulary version 5 files. These can be obtained from [Athena](http://athena.ohdsi.org).

Getting Started
===============
1. Get the latest version of the vocabulary from [Athena](http://athena.ohdsi.org).
2. Under the [Releases](https://github.com/OHDSI/Usagi/releases) tab, download Usagi*.jar.
3. Click on Usagi_vx.x.x.jar to start Usagi.
4. Usagi will prompt you to specify the location of the Vocabulary files to create the index (needs to be done only once).

The creation of the vocabulary index is a computationally expensive process and can take hours to complete. 
It has to be done only once and the resulting index files can be copied over to a new version of Usagi.

If the index creation does not finish within a few hours and seems to be stuck, please try to run the Usagi jar from the command line instead of clicking the jar (`java -jar Usagi_vx.x.x.jar`). Although this solution is not thoroughly tested, users have reported that this might help ([issue #64](https://github.com/OHDSI/Usagi/issues/64)).

Getting Involved
=============
* User guide and Help: <a href="http://www.ohdsi.org/web/wiki/doku.php?id=documentation:software:usagi">Usagi Wiki</a>
* Developer questions/comments/feedback: <a href="http://forums.ohdsi.org/c/developers">OHDSI Forum</a>
* We use the <a href="../../issues">GitHub issue tracker</a> for all bugs/issues/enhancements

License
=======
Usagi is licensed under Apache License 2.0.

Development
===========
Usagi is being developed in Eclipse. Contributions are welcome.
### Development status
Ready for use.

Acknowledgements
================
Martijn Schuemie is the author of this application.
