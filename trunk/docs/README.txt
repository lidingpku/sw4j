==Introduction==
This pages is written for better java based semantic web programming.

[[author::Li Ding]]
[[license::MIT License]]
[[version::0.5]]
[[status::alpha]]

==Prerequisite==
required
* java 1.5.15 or up
* jena   jena-2.5.7
* Pellet.  pellet-2.0.0-rc5.zip (Mar 3, 2009), http://clarkparsia.com/pellet/download/pellet-2.0.0-rc5
* GoogleSearch SOAP API. - googleapi.jar  It was deprecated by google though. it is used to guess charset encoding.


==Package Structure==
core - sw4j.util 
* web  - web access tools 
* rdf  - rdf manipulation tools (based on jena)

vocabulary - sw4j.vocabulary
* pml - pml vocabulary

extensions - sw4j.task 
* common - the shared classes
* load - loading data from the web, file, or string
* rdf - smartly parse RDF from documents, e.g. RDF/XML, RDFa, embedded RDF, gzipped RDF
* oie - evaluate issues with owl instance data using referenced ontology
* util - the frequently used tools 


==TODO==
* need better unit test scripts


==Change log==
* April 23, 2009. v0.5  first alpha release. 