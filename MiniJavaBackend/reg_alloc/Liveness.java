package reg_alloc;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.LinkedList;

import flow_graph.FlowGraph;
import graph.Node;
import temp.Temp;
import util.List;

public class Liveness extends InterferenceGraph
{
    private FlowGraph graph;
    
    public void addEdge(Node src, Node dst)
    {
        if ( src != dst && !dst.comesFrom(src) && !src.comesFrom(dst))
            super.addEdge(src, dst);
    }
    
    // estruturas usadas para computar a DFA
    private Hashtable<Node, HashSet<Temp>> in;
    private Hashtable<Node, HashSet<Temp>> out;
    private Hashtable<Node, HashSet<Temp>> gen;
    private Hashtable<Node, HashSet<Temp>> kill;
        
    public void show(PrintStream o)
    {       
        for ( List<Node> aux = this.nodes(); aux != null; aux = aux.tail )
        {
            Temp t = revMap.get(aux.head);
            
            o.print(t + ": [ ");
            for ( List<Node> adjs = aux.head.adj(); adjs != null; adjs = adjs.tail )
                o.print( revMap.get(adjs.head) + " ");
            o.println("]");
        }
    }
    
    // coisas uteis
    private MoveList moveList = null;
    
    private Hashtable<Node, Temp> revMap = new Hashtable<Node, Temp>();
    private Hashtable<Temp, Node> map = new Hashtable<Temp, Node>();
    
    public Liveness(FlowGraph g)
    {
        super();
    
        graph = g;
            
        computeGenKill();
        computeDFA();
        buildInterference();
    }

    public void dump(PrintStream outStream)
    {
        int c=0;
        for(List<Node> aux = graph.nodes(); aux != null; aux = aux.tail, c++)
        {
            HashSet<Temp> i = in.get(aux.head);
            HashSet<Temp> o = out.get(aux.head);
            HashSet<Temp> g = gen.get(aux.head);
            HashSet<Temp> k = kill.get(aux.head);
            
            outStream.println(c+": gen:"+g+" kill:"+k+" in:"+i+" out:"+o);
        }
    }
    
    private void computeGenKill()
    {
        kill = new Hashtable<Node, HashSet<Temp>>();
        gen  = new Hashtable<Node, HashSet<Temp>>();
        
        for(List<Node> nodes = graph.nodes(); nodes != null; nodes = nodes.tail)
        {
            HashSet<Temp> k = new HashSet<Temp>();
            HashSet<Temp> g = new HashSet<Temp>();
            
            // kill
            for ( List<Temp> aux = graph.def(nodes.head); aux != null; aux = aux.tail )
                k.add(aux.head);
            
            // gen
            for ( List<Temp> aux = graph.use(nodes.head); aux != null; aux = aux.tail )
                g.add(aux.head);
            
            kill.put(nodes.head, k);
            gen.put(nodes.head, g);
        }
    }
    
    private void computeDFA()
    {	
        Node first = graph.nodes().head;
		
        in = new Hashtable<Node, HashSet<Temp>>();
        out = new Hashtable<Node, HashSet<Temp>>();

		Hashtable<Node, HashSet<Temp>> oldIn;
		Hashtable<Node, HashSet<Temp>> oldOut;

		do 
		{
			oldIn = (Hashtable<Node, HashSet<Temp>>)in.clone();
			oldOut = (Hashtable<Node, HashSet<Temp>>)out.clone();

			for(List<Node> nodes = graph.nodes(); nodes != null; nodes = nodes.tail)
			{
				computeDFAIn(nodes.head);
				computeDFAOut(nodes.head);
			}

		} while (!oldIn.equals(in) || !oldOut.equals(out));
    }
    
	public HashSet<Temp> computeDFAIn(Node n)
	// Return the in hashset
	{
		// when in of n is empty, start by putting the use set
        HashSet<Temp> i = in.get(n);
		if (i == null)
		{
			i = (HashSet<Temp>)gen.get(n).clone();
			in.put(n, i);
		}

		// gets out set and clone it to not modify the out set
        HashSet<Temp> o = out.get(n);
		if (o == null)
		{
			o = new HashSet<Temp>();
			out.put(n, o);
		}
		else
			o = (HashSet<Temp>)o.clone();

		// do out() - def() set operation
		Iterator itr = kill.get(n).iterator();
		while (itr.hasNext())
			o.remove((Temp)itr.next());

		itr = o.iterator();
		while (itr.hasNext())
			i.add((Temp)itr.next());

		return o;
	}

	public void computeDFAOut(Node n)
	{
        for(List<Node> nodes = n.succ(); nodes != null; nodes = nodes.tail)
		{
			Node succ = nodes.head;
            HashSet<Temp> o = in.get(succ);
			// in of succ not defined, compute the in parameter so we can do the out set of n
			if (o == null)
				o = computeDFAIn(succ);

			out.put(n, o);
		}
	}

    private Node getNode(Temp t)
    {
        Node n = map.get(t);
        
        if ( n == null )
        {
            n = this.newNode();
            
            map.put(t, n);
            revMap.put(n, t);
        }
        
        return n;
    }
    
    private void handle(Node instr)
    {
        for( List<Temp> defs = graph.def(instr); defs != null; defs = defs.tail )
        {
            Node currentTemp = this.getNode(defs.head);
            
            for( Temp liveOut : out.get(instr) )
            {                
                Node currentLiveOut = this.getNode(liveOut);
                this.addEdge(currentTemp, currentLiveOut);
            }
        }
    }
    
    private void handleMove(Node instr)
    {
        Node dst = this.getNode(graph.def(instr).head);
        Node src = this.getNode(graph.use(instr).head);
        
        moveList = new MoveList(src, dst, moveList);
        
        for( Temp t : out.get(instr) )
        {
            Node currentOut = this.getNode(t);
            
            if ( currentOut != src )
            {
                //this.addEdge(currentOut, dst);
                this.addEdge(dst, currentOut);
            }
        }
    }
    
    private void buildInterference()
    {
        // Estamos sentados sobre ombros de um gigante...
        // Aqui, nos temos uma lista sobre todos os temporarios
        // vivos no fim de cada no. Desta forma, eh relativamente
        // facil construir a lista de adjacencia.
        
        for ( List<Node> instrs = graph.nodes(); instrs != null; instrs = instrs.tail )
        {
            Node current = instrs.head;
            
            if ( graph.isMove(current))
                handleMove(current);
            else
                handle(current);
        }
    }
    
    public Node tnode(Temp temp)
    {
        Node n = map.get(temp);
        
        if ( n == null )
        {
            map.put(temp, n = newNode() );
            revMap.put(n, temp);
        }
        
        return n;
    }

    public Temp gtemp(Node node)
    {
        return revMap.get(node);
    }

    public MoveList moves()
    {
        return moveList;
    }

    public HashSet<Temp> Out(Node node)
    {
        return out.get(node);
    }    
}
