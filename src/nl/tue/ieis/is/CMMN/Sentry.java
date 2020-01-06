package nl.tue.ieis.is.CMMN;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jacop.constraints.And;
import org.jacop.constraints.Not;
import org.jacop.constraints.PrimitiveConstraint;
import org.jacop.constraints.XeqC;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.core.Var;
import org.jdom.Element;
import org.jdom.Namespace;

public class Sentry extends CMMNElement {
	private Set<onPart> ops;
	private String ifPart;
	String name;
	
	public Sentry(){
		ops=new HashSet<onPart>();
		ifPart="";
	}
	
	public void setName(String s){
		name=s;
	}
	
	public String getName(){
		return name;
	}
	
		
	public void addOnPart(onPart op){
		ops.add(op);
	}
	
	public void addOnParts(Set<onPart> opsa){
		ops.addAll(opsa);
	}


	public void setGuard(String g){
		ifPart=g;
	}
	
	public String getGuard(){
		return ifPart;
	}
	
	public Set<onPart> getOnParts(){
		return ops;
	}
	
	
	public boolean isGround(){
		String orig="\\borig\\b";
		Pattern pattern=Pattern.compile(orig);
		Matcher matcher=pattern.matcher(ifPart);
		boolean origOccurs=matcher.find();
		return !origOccurs;
	}
	
	public boolean isCopy(){
		return ifPart.equals("orig")&&ops.isEmpty();
	}
	
	public boolean isMerge(){
		String and="(\\band\\b)|(&&)";
		Pattern pattern=Pattern.compile(and);
		String [] ifParts=ifPart.split(and,0);
		if (ifParts.length==1) {
			if (ops.size()==0) {// no event
				return false;
			}
		}
		boolean found=false;
		for (String s:ifParts) {
			if (s.trim().equals("orig")) found=true;
		}
		return found;
	}
	
	
	public Set<PlanItem> computeSourceReferences(){
		Set<PlanItem> references=new HashSet();
		for (onPart o:ops){
			PlanItem pi=o.getSource();
			references.add(pi);
		}
		return references;
	}
	
	public void print(int indent){	
		System.out.println(this.getId());
		for (int i=0;i<indent;i++) System.out.print(" ");
		if (!ops.isEmpty()){
			System.out.println("OnParts");
			for (onPart e:ops){
				e.print(indent+2);
			}
		}
		if (!ifPart.isEmpty()){
			System.out.println("Guard: "+ifPart);
		}
		else{
			System.out.println("Empty guard");
		}
	}
	
		
	Element printElement(Namespace ns){
		Element el=new Element("sentry");
		el.setNamespace(ns);
		el.setAttribute("id",getId());
		if (name!=null){
			el.setAttribute("name",name);
		}
		for (onPart e:ops){			
			Element el2=new Element("planItemOnPart");
			el2.setNamespace(ns);
			el2.setAttribute("id",e.getId());
			el2.setAttribute("sourceRef",e.getSourceRef());
			
			Element el3=new Element("standardEvent");
			el3.setNamespace(ns);
			if (e.getStandardEventText()!=null){
				el3.addContent(e.getStandardEventText());
			}				
			el.addContent(el2);
			el2.addContent(el3);
		}
		if (getGuard()!=null){
			Element el4=new Element("ifPart");
			el4.setNamespace(ns);
			Element el5=new Element("condition");
			el5.addContent(getGuard());
			el5.setNamespace(ns);
			el4.addContent(el5);
			el.addContent(el4);
		}
		return el;
	}
}
