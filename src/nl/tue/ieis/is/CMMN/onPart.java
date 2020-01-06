package nl.tue.ieis.is.CMMN;

public class onPart extends CMMNElement {

	private String sourceref;
	private PlanItem source;
	private boolean direction;
	private boolean isStage;
	private boolean isMilestone;
	private String standardEventText;

	
	public onPart(){
		direction=true;
		isStage=false;
		isMilestone=false;
	}
	
	public onPart clone() {
		onPart copy=new onPart();
		copy.setSourceRef(this.getSourceRef());
		copy.setSource(this.getSource());
		copy.setDirection(this.returnDirection());
		copy.isStage=this.isStage;
		copy.isMilestone=this.isMilestone;
		copy.standardEventText=this.standardEventText;
		return copy;
	}
	
	public void setSourceRef(String s){
		sourceref=s;
	}
	
	public String getSourceRef(){
		return sourceref;
	}
	
	public void setSource(PlanItem t){ // task , stge or mielstone, referred to by sourceref
		source=t;
	}
	
	public PlanItem getSource(){
		return source;
	}
	
	public void setDirection(boolean plus){
		direction=plus;
	}
	
	public void setMilestoneSource(){
		isMilestone=true;
	}
	
	public void setStageSource(){
		isStage=true;
	}
	
	public boolean isStage(){
		return isStage;
	}
	
	public boolean isMilestone(){
		return isMilestone;
	}
	
	public boolean isStageMilestone(){
		return isStage || isMilestone;
	}
	
	public boolean returnDirection(){
		return direction;
	}
	
	public void setStandardEventText(String s){
		this.standardEventText=s;
	}
	
	public String getStandardEventText(){
		return this.standardEventText;
	}
	
	public void print(int indent){
		for (int i=0;i<indent;i++) System.out.print(" ");
		System.out.println("Event with sourceref\t"+this.sourceref);

		if (source!=null){
			System.out.println("Source:\t"+source.getName());
		}
		else{
			System.out.println("Empty source");
		}
	}
	
	public String generateSMV(){
		String s="";
		if (source!=null){
			s+=source.getName();
		}
		return s;
	}
}
