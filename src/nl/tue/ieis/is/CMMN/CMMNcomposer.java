package nl.tue.ieis.is.CMMN;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;

public class CMMNcomposer {

	
	public static void main(String args[]){
		if (args.length>0){
			String file=args[0];
			try{
				FileReader fr= new FileReader(args[0]);
				BufferedReader br = new BufferedReader(fr);

				ArrayList<CaseSchema> filelist=new ArrayList<CaseSchema>();

				String sCurrentLine;

				while ((sCurrentLine = br.readLine()) != null) {
			  		if (!sCurrentLine.equals("")){
			  			File f = new File(sCurrentLine);
			  			String fn=""; // full filename
			  			try {
			  				fn = f.toURI().toURL().toString();
			  			} catch (MalformedURLException e1) {
			  				// TODO Auto-generated catch block
			  				e1.printStackTrace();
			  			}
			  			fn=sCurrentLine;
			  			String name=fn.substring(0,fn.indexOf(".cmmn"));
			  			filelist.add(CMMNreader.doc2cmmn(CMMNreader.getDocument(fn),name));
			  		}
				}
				boolean wellformed=true;
				for (int i=0;i<filelist.size();i++){
					CaseSchema csi=filelist.get(i);
					HashSet <PlanItem> pis=(HashSet<PlanItem>) csi.getPlanItems();
					for (PlanItem pi:pis) {
						boolean refined=false; // refined=false means introduced
						for (int j=0;j<i;j++) {
							CaseSchema csj=filelist.get(j);
							PlanItem pij=csj.findSimilarPlanItem(pi);
							if (pij!=null){
								refined=true; // pi is refined
							}	
						}
						if (!refined) { // pi is introduced in csi
							boolean grounded=pi.isGroundForm(false)&&pi.isGroundForm(true);
							if (!grounded) {
								System.out.println("Case schema " + csi.getName() + " introduces but does not define " + pi.getPlanItemDefinition().getName());
								wellformed=false;
							}
						}
						else {
							// search previous occurrences of similar plan items
							boolean first=true;
							for (int j=0;j<i;j++) {
								CaseSchema csj=filelist.get(j);
								PlanItem pij=csj.findSimilarPlanItem(pi);
								if (pij!=null){
									if (first) {
										first=false;
									}
									else { // csj refines pi, so analyze independence of pij and pi
										if (!pij.analyzeMatchingSentrySetForms(pi)) {
											System.out.println("Case schemas " + csj.getName() + " and " + csi.getName() + " both refine plan item " + pi.getPlanItemDefinition().getName() + " but do not have matching sentry set forms.");
											wellformed=false;
										}
									}
								}
							}
						}
					}
				}
				if (wellformed) {
					System.out.println("The composition is well-formed.");
				}
				else {
					System.out.println("The composition is not well-formed.");
				}
				CaseSchema comp=null,add=null;

				for (int i=0;i<filelist.size();i++){
					 add=filelist.get(i);
					 //					
					 if (comp==null){
						 comp=add;
						 comp.setName(add.getName());
					 }
					 else{
						 comp.setName(comp.getName()+"-"+add.getName());
						 comp.compose(add);
					 }
				}
  				comp.setName(comp.getName()+"-comp");
  				System.out.println("File written to: "+comp.getName() + ".cmmn");
				CMMNwriter cw= new CMMNwriter();
	 			try {
					cw.writeFileUsingJDOM(comp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else {
			System.out.println("Usage: CMMNcomposer <file>");
		}
	}
}
