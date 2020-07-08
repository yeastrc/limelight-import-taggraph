pLink 2.x to ProXL XML Converter
==================================

Use this program to convert the results of a pLink 2.x cross-linking analysis to Proxl XML suitable for import into the proxl web application.

Note: If you are using pLink 1.x, please go to [pLink 1.x converter](https://github.com/yeastrc/proxl-import-plink)

Currently only unlabeled data using a single linker are supported by this converter. If you are using isotope labels, quantification,
or multiple linkers with your pLink 2.x data, please email us at mriffle .at. uw.edu. We will be happy to work with you to get your data supported.


How To Run
-------------
1. Download the [latest release](https://github.com/yeastrc/proxl-import-plink2/releases).
2. Run the program ``java -jar plink2toProxlXML.jar`` with no arguments to see the possible parameters.
3. Run the program, e.g., ``java -jar plink2toProxlXML.jar -p c:\plink_run\run_name.plink -o c:\output\output.proxl.xml -f c:\fastas\myFasta.fasta``

In the above example, ``output.proxl.xml`` will be created and be suitable for import into ProXL.

For more information on importing data into Proxl, please see the [Proxl Import Documentation](http://proxl-web-app.readthedocs.io/en/latest/using/upload_data.html).

More Information About Proxl
-----------------------------
For more information about Proxl, visit http://proxl-ms.org/.
