package nl.tue.ieis.is.CMMN;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


import org.jacop.core.BooleanVar;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jdom.Element;
import org.jdom.Namespace;


public class PlanItem extends CMMNElement{
	private String name;
	private String definitionRef;
	private Set<Criterion> entryRef;
	private Set<Criterion> exitRef;
	private Stage context;
	

	public PlanItem(Stage s){
		context=s;
		entryRef=new HashSet<Criterion>();
		exitRef=new HashSet<Criterion>();
	}
	
	public void setName(String n){
		name=n; //.replace(" ","_");
	}

	public String getName(){
		return name;
	}
	
	public void setDefinitionRef(String n){
		definitionRef=n;
	}
	
	public String getDefinitionRef(){
		return definitionRef;
	}
	
	public Stage getContext(){
		return context;
	}
	
	public void setContext(Stage s){
		context=s;
	}
	
	public PlanItemDefinition getPlanItemDefinition(){
		return context.findPlanItemDefinition(definitionRef);
	}
	
	public boolean isSimilar(PlanItem piOther) {
		return this.getPlanItemDefinition().getName().equals(piOther.getPlanItemDefinition().getName());
	}
		
	public void addEntryRef(Criterion n){
		entryRef.add(n);
	}

	public void setEntryRefs(Set<Criterion> ers){
		entryRef=ers;
	}
	
	public Set<Criterion> getEntryRefs(){
		return entryRef;
	}
	
	public void addExitRef(Criterion n){
		exitRef.add(n);
	}

	public Set<Criterion> getExitRefs(){
		return exitRef;
	}
	
	public void setExitRefs(Set<Criterion> ers){
		exitRef=ers;
	}

	
	public boolean isStrictCompositionForm(boolean entry){// false is exit
		return isCompositionForm(entry)&&!isExtensionForm(entry);
	}
	
	public boolean isStrictExtensionForm(boolean entry){// false is exit
		return !isCompositionForm(entry)&&isExtensionForm(entry);
	}

	
	public boolean isCompositionForm(boolean entry){// false is exit
		if (entry){
			boolean found=false;
			for (Criterion cc:entryRef){
				found=true;
				String sidc=cc.getSentryRef();
				Sentry sec=context.getSentry(sidc);
				if (!sec.isCopy()&&!sec.isMerge()) return false;
			}
			return found;
		}
		else{
			boolean found=false;
			for (Criterion cc:exitRef){
				found=true;
				String sidc=cc.getSentryRef();
				Sentry sec=context.getSentry(sidc);
				if (!sec.isCopy()&&!sec.isMerge()) return false;
			}
			return found;		
		}
	}
	
	public boolean isExtensionForm(boolean entry){// false is exit
		if (entry){
			boolean found=false;
			for (Criterion cc:entryRef){
				String sidc=cc.getSentryRef();
				Sentry sec=context.getSentry(sidc);
				if (sec.isCopy()) found=true;
				if (!sec.isCopy()&&!sec.isGround()) return false;
			}
			return found;
		}
		else{
			boolean found=false;
			for (Criterion cc:exitRef){
				String sidc=cc.getSentryRef();
				Sentry sec=context.getSentry(sidc);
				if (sec.isCopy()) found=true;
				if (!sec.isCopy()&&!sec.isGround()) return false;
			}
			return found;
		}
	}
	
	
	public boolean isGroundForm(boolean entry){// false is exit
		if (entry){
			if (!entryRef.isEmpty()) {
				for (Criterion cc:entryRef){
					String sidc=cc.getSentryRef();
					Sentry sec=context.getSentry(sidc);
					if (sec.isMerge()) {
						return false;
					}
					if (sec.isCopy()) {
						return false;
					}

				}
				return true;
			}
		}
		else{
			if (!exitRef.isEmpty()) {
				for (Criterion cc:exitRef){
					String sidc=cc.getSentryRef();
					Sentry sec=context.getSentry(sidc);
					if (sec.isMerge()) return false;
					if (sec.isCopy()) return false;
				}
				return true;
			}
		}
		return true; // empty entry or exit criterion; assumed to be [true]
	}
	
	public boolean analyzeMatchingSentrySetForms(PlanItem pi){
		boolean entryExtensionMatch=isExtensionForm(true) && pi.isExtensionForm(true);
		boolean entryCompositionMatch=isCompositionForm(true) && pi.isCompositionForm(true);
		boolean status=true;
		if (!entryExtensionMatch&&!entryCompositionMatch){
			if (entryRef.isEmpty()&&pi.entryRef.isEmpty()) {
				// do nothing
			}
			else {
				System.out.print("Warning: the entry sentries of plan item "+ this.getPlanItemDefinition().getName());
				System.out.print("\nin case schemas "+ this.getContext().getCaseContext().getName() + " and " + pi.getContext().getCaseContext().getName());
				System.out.println(" do not having matching forms.");
				status=false;
			}
		}
		boolean exitExtensionMatch=isExtensionForm(false) && pi.isExtensionForm(false);
		boolean exitCompositionMatch=isCompositionForm(false) && pi.isCompositionForm(false);
		if (!exitExtensionMatch&&!exitCompositionMatch){
			if (exitRef.isEmpty()&&pi.exitRef.isEmpty()) {
				// do nothing
			}
			else {
				System.out.print("Warning: the exit sentries of plan items "+ this.getPlanItemDefinition().getName());
				System.out.print("\nin case schemas "+ this.getContext().getCaseContext().getName() + " and " + pi.getContext().getCaseContext().getName());
				System.out.println(" do not having matching forms.");
				status=false;
			}
		}
		return status;
	}
	
	
	
	
	
	
	// precondition: pi and this are same, i.e., have the same planitemdefs, i.e., planitemdefs with the same name
	public void compose(PlanItem pi){
		Stage pi_context=pi.getContext();
		Set<Criterion> pi_entryRef=pi.getEntryRefs();
		Set<Criterion> new_entryRef=new HashSet<Criterion>();
		Set<Criterion> old_entryRef=new HashSet<Criterion>();
		Set<Sentry> new_sentries=new HashSet<Sentry>();
		Set<Sentry> old_sentries=new HashSet<Sentry>();
		for (Criterion c:entryRef){
			String sid=c.getSentryRef();
			Sentry se=context.getSentry(sid);
			old_sentries.add(se);
		}
		String orig = "\\borig\\b";
		for (Criterion c:pi_entryRef){
			String sid=c.getSentryRef();
			Sentry se=pi_context.getSentry(sid);
			Pattern pattern=Pattern.compile(orig);
			Matcher matcher=pattern.matcher(se.getGuard());
			boolean origOccurs=matcher.find();
			if (origOccurs){
				old_entryRef.add(c);
				for (Criterion cc:entryRef){
					String sidc=cc.getSentryRef();
					Sentry sec=context.getSentry(sidc);
					Sentry se_new=new Sentry();
					for (onPart o:se.getOnParts()) {
						se_new.addOnPart(o.clone());
					}
					for (onPart o:sec.getOnParts()) {
						se_new.addOnPart(o.clone());
					}
					String ifPart=se.getGuard();
					if (!sec.getGuard().isEmpty()){
						ifPart=ifPart.replaceAll(orig, sec.getGuard());
					}
					else{//sec only contains events, no guards
						ifPart=ifPart.replaceAll(orig, "true");
					}
					se_new.setGuard(ifPart);
					Criterion c_new=new Criterion(pi);
					c_new.setSentryRef(se_new.getId());
					new_entryRef.add(c_new);

					new_sentries.add(se_new);
				}
			}
			else{
				new_sentries.add(se);
				Criterion c_new=new Criterion(this);
				c_new.setSentryRef(sid);
				new_entryRef.add(c_new);
			}			
		}
		setEntryRefs(new_entryRef);
		context.removeSentries(old_sentries);
		context.addSentries(new_sentries);
		
		Set<Criterion> pi_exitRef=pi.getExitRefs();
		Set<Criterion> new_exitRef=new HashSet<Criterion>();
		Set<Criterion> old_exitRef=new HashSet<Criterion>();
		new_sentries=new HashSet<Sentry>();
		old_sentries=new HashSet<Sentry>();
		for (Criterion c:exitRef){
			String sid=c.getSentryRef();
			Sentry se=context.getSentry(sid);
			old_sentries.add(se);
		}

		for (Criterion c:pi_exitRef){
			String sid=c.getSentryRef();
			Sentry se=pi_context.getSentry(sid);
			Pattern pattern=Pattern.compile(orig);
			Matcher matcher=pattern.matcher(se.getGuard());
			boolean origOccurs=matcher.find();
			if (origOccurs){
				old_exitRef.add(c);
				for (Criterion cc:exitRef){
					String sidc=cc.getSentryRef();
					Sentry sec=context.getSentry(sidc);

					Sentry se_new=new Sentry();
					for (onPart o:se.getOnParts()) {
						se_new.addOnPart(o.clone());
					}
					for (onPart o:sec.getOnParts()) {
						se_new.addOnPart(o.clone());
					}
					String ifPart=se.getGuard();
					if (!sec.getGuard().isEmpty()){
						ifPart=ifPart.replaceAll(orig, sec.getGuard());
					}
					else{//sec only contains events, no guards
						ifPart=ifPart.replaceAll(orig, "true");
					}
					se_new.setGuard(ifPart);
					Criterion c_new=new Criterion(pi);
					c_new.setSentryRef(se_new.getId());
					new_exitRef.add(c_new);
					
					new_sentries.add(se_new);
				}
			}
			else{
				new_sentries.add(se);
				Criterion c_new=new Criterion(this);
				c_new.setSentryRef(sid);
				new_exitRef.add(c_new);
			}			
		}
		setExitRefs(new_exitRef);
		context.removeSentries(old_sentries);
		context.addSentries(new_sentries);

	}
	
	public void printEntrySentries(){
		System.out.println("Entry sentries");
		for (Criterion cc:entryRef){
			String sidc=cc.getSentryRef();
			Sentry sec=context.getSentry(sidc);
			sec.print(0);
		}
	}
	
	public void printExitSentries(){
		System.out.println("Exit sentries");
		for (Criterion cc:exitRef){
			String sidc=cc.getSentryRef();
			Sentry sec=context.getSentry(sidc);
			sec.print(0);
		}
	}

	
	public void print(int indent){
		for (int i=0;i<indent;i++) System.out.print(" ");
		System.out.println("Name:\t"+name);
		for (int i=0;i<indent;i++) System.out.print(" ");
		System.out.println("Id:\t"+getId());
		for (int i=0;i<indent;i++) System.out.print(" ");
		System.out.println("DefinitionRef: "+definitionRef);
		for (int i=0;i<indent;i++) System.out.print(" ");
		System.out.println("Context:\t"+context.getId());
		if (!entryRef.isEmpty()){
			for (int i=0;i<indent;i++) System.out.print(" ");
			System.out.println("begin of entryref criteria\t");
			for (Criterion se:entryRef){
				for (int i=0;i<indent;i++) System.out.print(" ");
				System.out.print(se.getSentryRef()+"\n");
			}
			for (int i=0;i<indent;i++) System.out.print(" ");
			System.out.println("end of entryref criteria\t");
		}
		if (!exitRef.isEmpty()){
			for (int i=0;i<indent;i++) System.out.print(" ");
			System.out.println("begin of exitref criteria\t");
			for (Criterion se:exitRef){
				for (int i=0;i<indent;i++) System.out.print(" ");
				System.out.print(se.getSentryRef()+"\n");
			}
			for (int i=0;i<indent;i++) System.out.print(" ");
			System.out.println("end of exitref criteria\t");
		}
	}
	
	public Element printElement(Namespace ns){
		Element planItem=new Element("planItem",ns);
		planItem.setAttribute("id", getId());
		planItem.setAttribute("definitionRef",getDefinitionRef());
		for (Criterion c:entryRef){
			Element el2=new Element("entryCriterion");
			el2.setNamespace(ns);
			el2.setAttribute("id",c.getId());
			el2.setAttribute("sentryRef",c.getSentryRef());
			planItem.addContent(el2);
		}
		for (Criterion c:exitRef){
			Element el2=new Element("exitCriterion");
			el2.setNamespace(ns);
			el2.setAttribute("id",c.getId());
			el2.setAttribute("sentryRef",c.getSentryRef());
			planItem.addContent(el2);
		}		
		return planItem;
	}
	
	
	public Set<PlanItem> getSourceReferences(){
		Set<PlanItem> pis=new HashSet();
		for (Criterion c:entryRef){
			Sentry se=this.getContext().getSentry(c.getSentryRef());
			pis.addAll(se.computeSourceReferences());
		}
		return pis;
	}
	
}
