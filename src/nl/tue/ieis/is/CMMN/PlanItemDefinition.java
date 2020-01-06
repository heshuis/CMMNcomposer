package nl.tue.ieis.is.CMMN;

import java.util.HashSet;
import java.util.Set;

import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jdom.Element;
import org.jdom.Namespace;

public class PlanItemDefinition extends CMMNElement {
	private String name;
	private Stage context;
	
	public PlanItemDefinition(String n,Stage c){
		context=c; // null if planmodel (root)
		name=n;
	}
	
	public void setName(String n){
		name=n;
	}

	public String getName(){
		return name;
	}
	
	public Stage getContext(){
		return context;
	}
	
	public void setContext(Stage s){
		context=s;
	}

	public boolean isCompound(){
		return false;
	}
	
	public boolean equals(PlanItemDefinition pi){
		return pi.getName().equals(name)&&pi.getClass().equals(this.getClass());
	}
	
	public void compose(PlanItemDefinition pid){// nothing to do if milestone etc., only for stage
		return;
	}
	
	public boolean isStage(){
		return false;
	}
	
	public boolean isMilestone(){
		return false;
	}
	
	public boolean isTask(){
		return false;
	}
	
	public void print(int indent){
		for (int i=0;i<indent;i++) System.out.print(" ");
		System.out.println("Name:\t"+name);
		for (int i=0;i<indent;i++) System.out.print(" ");
		System.out.println("Id:\t"+getId());
		System.out.println(context.getCaseContext().getName());
	}
		
	public Element printElement(Namespace ns){// abstract class
		return null;
	}	
}
