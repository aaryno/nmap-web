package aaryn.nmap.scanner;

/**
 * List of possible port states encountered during a scan.
 * @author aaryno1
 *
 */
public enum PortState {
	OPEN,
	CLOSED,
	FILTERED,
	OPEN_FILTERED,
	CLOSED_FILTERED
}
