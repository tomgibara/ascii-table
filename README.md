ASCII table
===========

A small command-line Java application for generating a table of ASCII code and related information.

This project came about because I was unable to find a good quality ASCII reference table that contained the information I needed on a frequent/occasional basis, including:

* ASCII codes in both denary and hexadecimal
* Control characters for commandline entry
* HTML character entity references (which I still need occasionally)
* The Unicode aliases

I have used authoritative sources for the table information so any errors or omissions are mine.

I am sharing the code because it may prove useful to someone who wants to derive a similar table. It is licensed under an [Apache 2.0 Licence](https://raw.githubusercontent.com/tomgibara/ascii-table/master/LICENSE). It may also be useful to someone who's looking to learn how to use the Apache Batik library, though the code itself has been put together fairly hastily. 

Building
--------

This is a Java Maven project. Building should be as simple as:

1. Checking out the project with git, and
2. compiling the project with (eg. mvn package).

Running
-------

Running the application to generate the table:

`java -jar ascii-table-1.x-jar-with-dependencies png|svg <output-path>`

Note that both building and running the software require Java 1.7.

Output
------

Generated output may be freely licensed by the person generating it. Mine is licensed under a Creative Commons 'Attribution-ShareAlike' licence. See [the wiki](https://github.com/tomgibara/ascii-table/wiki) for details.

Finally, here's the table the application generates as a PNG.

![ASCII Table (PNG)](https://raw.githubusercontent.com/wiki/tomgibara/ascii-table/tables/ascii-table-1.1.png)

The table is also generated in SVG for high quality reproduction. Again, see [the wiki](https://github.com/tomgibara/ascii-table/wiki) for links. Note that SVG generation embeds a subset of the Cousine font (via data URLs in an inline stylesheet) to allow for reproducible renders on different systems, though this is contingent on full SVG CSS font support.
