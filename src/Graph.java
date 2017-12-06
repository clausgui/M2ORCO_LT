import java.util.LinkedList;
import java.util.List;

public class Graph {
	private double[][] distanceMatrix;
	private List<Vertex> vertices;
	private List<Edge> edges;
	
	public Graph(double[][] dm){
		this.distanceMatrix = dm;
		this.vertices = new LinkedList<Vertex>();
		this.edges = new LinkedList<Edge>();
	}
	
	public void addEdgeBetween(Vertex start, Vertex end, int clusterIndex){
		Edge e = new Edge(start, end, this.distanceMatrix[start.getIndex()][end.getIndex()]);
		e.setClusterIndex(clusterIndex);
		this.edges.add(e);
	}
	
	public void addVertex(Vertex v){
		this.vertices.add(v);
	}
	
	public void addVertices(Vertex[] v){
		
		int n = v.length;
		
		for(int i=0; i<n; i++){
			this.addVertex(v[i]);
		}
		
	}
	
	public void removeEdge(Edge e){
		this.edges.remove(e);
	}
	
	public void removeVertex(Vertex v){
		this.edges.remove(v.getIncomingEdge());
		this.edges.remove(v.getOutgoingEdge());
		this.vertices.remove(v);
	}
	
	public List<Edge> getEdges(){
		return this.edges;
	}
	
	public List<Vertex> getVertices(){
		return this.vertices;
	}
	
	public List<Edge> findEdgesFromOtherClusters(Vertex v){
		
		List<Edge> edges = new LinkedList<Edge>();
		
		for(int e=0; e<this.edges.size(); e++){
			if(this.edges.get(e).getClusterIndex() != v.getClusterIndex()){
				edges.add(this.edges.get(e));
			}
		}
		return edges;
		
	}
}
