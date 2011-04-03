package Action;
import Minijava.analysis.*;
import Minijava.node.*;
import java.util.*;

public class Translation extends DepthFirstAdapter
{
	private static Dictionary<TId, ClassData> classes = new Hashtable<TId, ClassData>();
	private static ClassData cl;
    public void caseAProg(AProg node){
        inAProg(node);
		PMainClass main = node.getMainClass();
        if(main != null){
			System.out.println(main.parent() instanceof PProg);
            main.apply(this);
			classes.put(main.getName(), cl);
		}
		else
			System.out.println("Error no main class found");


		//List<PClassDecl> copy = new ArrayList<PClassDecl>(node.getClassDecl());
		//for(PClassDecl e : copy){
		//	e.apply(this);
		//}
        //outAProg(node);
	}
	public void caseAMainClass(AMainClass node){
		ClassData main_cl = new ClassData()

		cl = main_cl
	}

}
