/**
 * Handles all aspects of interacting with nmap and gathering nmap results.
 * NmapScanner handles the actual scanning and saves it into an NmapScanSummary 
 * for further processing into both hibernate entities and the lightweight
 * POJOs that serve for json marshalling (package aaryn.nmap.summary).
 */
/**
 * @author aaryno1
 *
 */
package aaryn.nmap.scanner;