import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Vertex {
	
	private int index;
	private boolean isVisited;
	
	private Edge theIncomingEdge;
	private Edge theOutgoingEdge;
	private Vertex theDominatingNeighbour;
	private Vertex theDominatedNeighbour;
	
	private Vertex[] sortedOtherVertices;
	private int numberOfSites;
	private int clusterIndex;
    
	public Vertex(int i, int numberOfSites){
		this.index = i;
		
		this.theIncomingEdge = null;
		this.theOutgoingEdge = null;
		this.theDominatingNeighbour = null;
		this.theDominatedNeighbour = null;
		
		this.isVisited = false;
		this.sortedOtherVertices = new Vertex[numberOfSites];
		this.numberOfSites = numberOfSites;
	}
	
	public void joinIncomingEdge(Edge e, Vertex v){
		this.theIncomingEdge = e;
		this.theDominatingNeighbour = v;
	}
	
	public void joinOutgoingEdge(Edge e, Vertex v){
		this.theOutgoingEdge = e;
		this.theDominatedNeighbour = v;
	}
	
	public void removeIncomingEdge(){
		if(this.theIncomingEdge != null){
			this.theIncomingEdge = null;
			this.theDominatingNeighbour.removeOutgoingEdge();
			this.theDominatingNeighbour = null;
		}
	}
	
	public void removeOutgoingEdge(){
		if(this.theOutgoingEdge != null){
			this.theOutgoingEdge = null;
			this.theDominatedNeighbour.removeIncomingEdge();
			this.theDominatedNeighbour = null;
		}
	}
	
	public void removeAllEdges(){
		this.removeIncomingEdge();
		this.removeOutgoingEdge();
	}
	
	public Edge getIncomingEdge(){
		return this.theIncomingEdge;
	}
	
	public Edge getOutgoingEdge(){
		return this.theOutgoingEdge;
	}
	
	public void sortOthers(Vertex[] others, double[] distances){
		
		for(int i=0; i<this.numberOfSites; i++){
			this.sortedOtherVertices[i] = others[i];
		}
		
		Vertex v;
		double d;
		
		for(int i=0; i<this.numberOfSites-1; i++){
			for(int j=1; j<this.numberOfSites-i; j++){
				if(distances[j-1]>distances[j]){
					v = this.sortedOtherVertices[j-1];
					this.sortedOtherVertices[j-1] = this.sortedOtherVertices[j];
					this.sortedOtherVertices[j] = v;
					
					d = distances[j-1];
					distances[j-1] = distances[j];
					distances[j] = d;
				}
			}
		}
	}
	
	public Vertex getClosestUnvisitedVertex(){
		
		int i=1;
		Vertex v = null;
		
		while(i<this.numberOfSites){
			if(this.sortedOtherVertices[i].isVisited()==false){
				v = this.sortedOtherVertices[i];
				i = this.numberOfSites;
			}
			i++;
		}
		
		return v;
		
	}
	
	public boolean isVisited(){
		return this.isVisited;
	}
	
	public void markAsVisited(){
		this.isVisited = true;
	}
	
	public void markAsUnvisited(){
		this.isVisited = false;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public void setClusterIndex(int i){
		this.clusterIndex = i;
	}
	
	public int getClusterIndex(){
		return this.clusterIndex;
	}
	
	public Vertex getDominatedNeighbour(){
		return this.theDominatedNeighbour;
	}
	
	public Vertex getDominatingNeighbour(){
		return this.theDominatingNeighbour;
	}
	
	public double getTotalCostOfAdjacentEdges(){
		double cost = 0;
		
		cost += this.theIncomingEdge.getWeight();
		cost += this.theOutgoingEdge.getWeight();
		
		return cost;
	}
	
	public double getDistanceBetweenTwoNeighbours(double[][] dm){
		return dm[this.theDominatingNeighbour.getIndex()][this.theDominatedNeighbour.getIndex()];
	}
	
	public void disconnect(Graph g){
		g.addEdgeBetween(this.theDominatingNeighbour, this.theDominatedNeighbour, this.clusterIndex);
		g.removeEdge(this.theIncomingEdge);
		g.removeEdge(this.theOutgoingEdge);
		this.theIncomingEdge = null;
		this.theOutgoingEdge = null;
	}
	
	public void putBetween(Edge e, Graph g){
		this.setClusterIndex(e.getStart().getClusterIndex());
		g.removeEdge(e);
		g.addEdgeBetween(e.getStart(), this, this.clusterIndex);
		g.addEdgeBetween(this, e.getEnd(), this.clusterIndex);
	}
}
