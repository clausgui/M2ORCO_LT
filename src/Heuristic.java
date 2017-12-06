import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VF;
import globalct.WeightedCircuitFactory;

public class Heuristic {
	private Graph myGraph;
	private double[][] distanceMatrix;
	private double totalCost;
	private int numberOfSites;
	private Vertex[] myVertices;
	private List<List<Vertex>> clusters;
	private int clusterSize;
	private int[] sizeOfCluster;
	
	public Heuristic(double[][] dm, int n){
		this.distanceMatrix = dm;
		this.totalCost = 0;
		this.myGraph = new Graph(this.distanceMatrix); 
		this.numberOfSites = n;
		
		this.myVertices = new Vertex[n];
		
		for(int i=0; i<n; i++){
			this.myVertices[i] = new Vertex(i, this.numberOfSites);
		}
		
		for(int i=0; i<n; i++){
			this.myVertices[i].sortOthers(this.myVertices, this.getRow(i));
		}
		
		this.myGraph.addVertices(this.myVertices);
		
	}
	
	public void Solve(){
		/*for(int k=1; k<this.numberOfSites; k++){
			this.findBestClustering(k);
		}*/
		this.initializeClustersAndCalculateTheirTours(2);
		this.improveClusters();
	}
	
	private double initializeClustersAndCalculateTheirTours(int k){
		
		this.clusterSize = this.numberOfSites/k; //To be reconsidered!!!
		this.sizeOfCluster = new int[k];
		
		for(int i=0; i<k; i++){
			this.sizeOfCluster[i] = this.clusterSize;
		}
		
		if(k==1){
			return this.totalCost;
		}
		else{
			
			this.clusters = new LinkedList<List<Vertex>>();
			
			for(int i=0; i<k; i++){
				List<Vertex> cluster = new LinkedList<Vertex>();
				this.clusters.add(cluster);
				
				Vertex v0 = this.findUnvisitedVertex();
				this.clusters.get(i).add(v0);
				v0.setClusterIndex(i);
				v0.markAsVisited();
				for(int n=0; n<this.clusterSize-1; n++){
					Vertex vi = v0.getClosestUnvisitedVertex();
					this.clusters.get(i).add(vi);
					vi.setClusterIndex(i);
					vi.markAsVisited();
				}

				this.totalCost += this.findTourOfCluster(i);
				
			}
			this.printClusters(k);
			System.out.println("Total Cost: "+this.totalCost);
			return this.totalCost;
		}
	}
	
	private void improveClusters(){
		
		while(this.gettingBetter()){
			this.printToursOfClusters(2);
		}
		
	}
	
	private boolean gettingBetter(){
		for(int i=0; i<this.numberOfSites; i++){
			Vertex v = this.myVertices[i];
			
			Iterator<Edge> e = this.myGraph.findEdgesFromOtherClusters(v).iterator();
			
			while(e.hasNext()){
				Edge currentEdge = e.next();
				if(v.getTotalCostOfAdjacentEdges() + currentEdge.getWeight()
				 > v.getDistanceBetweenTwoNeighbours(this.distanceMatrix)
				 + this.distanceMatrix[v.getIndex()][currentEdge.getStart().getIndex()]
				 + this.distanceMatrix[v.getIndex()][currentEdge.getEnd().getIndex()]){
					v.disconnect(this.myGraph);
					this.sizeOfCluster[v.getClusterIndex()]--;
					v.putBetween(currentEdge, this.myGraph);
					this.sizeOfCluster[v.getClusterIndex()]++;
					return true;
				}
			}
		}
		return false;
	}
	
	/*private boolean allVisited(){
		boolean allVisited = true;
		
		for(int i=0; i<this.numberOfSites; i++){
			if(this.myVertices[i].isVisited()==false){
				allVisited = false;
				i = this.numberOfSites;
			}
		}
		
		return allVisited;
	}*/
	
	private Vertex findUnvisitedVertex(){
		
		Vertex v = null;
		
		for(int i=0; i<this.numberOfSites; i++){
			if(this.myVertices[i].isVisited()==false){
				v = this.myVertices[i];
				i = this.numberOfSites;
			}
		}
		
		return v;
	}
	
	private double[] getRow(int i){
		double[] row = new double[this.numberOfSites];
		for(int j=0; j<this.numberOfSites; j++){
			row[j] = this.distanceMatrix[i][j];
		}
		return row;
	}
	
	private void printClusters(int k){
		
		for(int i=0; i<k; i++){
			
			System.out.println("Cluster "+(i+1)+":");
			
			List<Vertex> vertexCluster = this.clusters.get(i);
			
			Iterator<Vertex> it = vertexCluster.iterator();
			
			while(it.hasNext()){
				Vertex currentVertex = it.next();
				System.out.print(currentVertex.getIndex()+" ");
			}
			
			System.out.println();
			
		}
		
	}
	
	private void printToursOfClusters(int k){
		
		for(int i=0; i<k; i++){
			
			System.out.println("Tour of Cluster "+(i+1)+":");
			
			List<Vertex> vertexCluster = this.clusters.get(i);
			
			Vertex initialVertex = vertexCluster.get(0);
			Vertex iteratedVertex = initialVertex.getDominatedNeighbour();
			System.out.print(initialVertex.getIndex()+ "-> "+iteratedVertex.getIndex());			
			
			Iterator<Vertex> it = vertexCluster.iterator();
			
			while(!iteratedVertex.equals(initialVertex)){
				System.out.print(" -> "+iteratedVertex.getDominatedNeighbour().getIndex());
				iteratedVertex = iteratedVertex.getDominatedNeighbour();
			}
			
			System.out.println();
			
		}
		
	}
	
	private int[][] findDistanceMatrixOfCluster(int k){
		
		int N = //this.clusters.get(k).size();
				this.clusterSize;
		int[][] dm = new int[N][N];
		
		for(int i=0; i<N; i++){
			for(int j=0; j<N; j++){
				dm[i][j] = (int) this.distanceMatrix[this.clusters.get(k).get(i).getIndex()][this.clusters.get(k).get(j).getIndex()];
			}
		}
		
		return dm;
		
	}
	
	private int[] getIndicesOfCluster(int k){
		
		int N = //this.clusters.get(k).size();
				this.clusterSize;
		int[] indices = new int[N];
		
		for(int i=0; i<N; i++){
			indices[i] = this.clusters.get(k).get(i).getIndex();
		}
		
		return indices;
		
	}
	
	private int findTourOfCluster(int k) {
        
		int[][] dm = this.findDistanceMatrixOfCluster(k);
		int[] indices = this.getIndicesOfCluster(k);
		
		int n = //this.clusters.get(k).size();
				this.clusterSize;

        Solver solver = new Solver();
        IntVar   cost = VF.bounded("OBJ", 0, VF.MAX_INT_BOUND, solver);
        IntVar[] next = VF.integerArray("succ_", n, 0, n-1, solver);

        WeightedCircuitFactory.weightedCircuit(next, cost, dm);
        solver.findOptimalSolution(ResolutionPolicy.MINIMIZE, cost);
        
        try {
        	solver.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
        
        //Chatterbox.printStatistics(solver);
        System.out.println();
        for (int i = 0; i < next.length; i++) {
        	this.myGraph.addEdgeBetween(this.myVertices[indices[i]], this.myVertices[indices[next[i].getValue()]], k);
            System.out.println(indices[i] + " -> " + indices[next[i].getValue()]);
        }
        System.out.println("Distance: " + cost.getValue());
        
        return cost.getValue();
    }
	
}
