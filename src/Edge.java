
public class Edge {
	private Vertex start;
	private Vertex end;
	private double weight;
	private int clusterIndex;
	
	public Edge(Vertex start, Vertex end, double w){
		this.start = start;
		this.end = end;
		this.weight = w;
		start.joinOutgoingEdge(this, end);
		end.joinIncomingEdge(this, start);
	}
	
	public Vertex getStart(){
		return this.start;
	}
	
	public Vertex getEnd(){
		return this.end;
	}
	
	public double getWeight(){
		return this.weight;
	}
	
	public void setClusterIndex(int k){
		this.clusterIndex = k;
	}
	
	public int getClusterIndex(){
		return this.clusterIndex;
	}
}
