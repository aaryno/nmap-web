package aaryn.nmap.scanner;
/**
 * Exception we get when running the tool or encountering an error or strange state
 * @author aaryno1
 *
 */
public class NmapException extends Exception {

	public NmapException(String message){
		super(message);
	}
}
