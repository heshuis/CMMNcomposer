package nl.tue.ieis.is.CMMN;
import java.util.UUID;

public class CMMNElement {
	private String id;
	
	public CMMNElement(){
		id=UUID.randomUUID().toString();
	}
	
	public void setId(String s){
		id=s;
	}
	
	public String getId(){
		return id;
	}
	
}
