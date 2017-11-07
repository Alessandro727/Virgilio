package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.User;
import postgres.PersistenceException;
import postgres.UserPostgres;


public class KMeans 
{
	// Data members
	private double [][] _data; // Array of all records in dataset
	private int [] _label;  // generated cluster labels
	private double [][] _centroids; // centroids: the center of clusters
	private int _nrows, _ndims; // the number of rows and dimensions
	private int _numClusters; // the number of clusters;

	// Constructor; loads records from file <fileName>. 
	// if labels do not exist, set labelname to null
	public KMeans(double[][] data, int nrows, int ndims) 	{

		this._data =data;
		this._nrows = nrows;
		this._ndims = ndims;


	}

	// Perform k-means clustering with the specified number of clusters and
	// Eucliden distance metric. 
	// niter is the maximum number of iterations. If it is set to -1, the kmeans iteration is only terminated by the convergence condition.
	// centroids are the initial centroids. It is optional. If set to null, the initial centroids will be generated randomly.
	public void clustering(int numClusters, int niter, double [][] centroids) 
	{
		_numClusters = numClusters;
		if (centroids !=null)
			_centroids = centroids;
		else{
			// randomly selected centroids
			_centroids = new double[_numClusters][];

			ArrayList<Integer> idx= new ArrayList<Integer>();
			for (int i=0; i<numClusters; i++){
				int c;
				do{
					c = (int) (Math.random()*_nrows);
				}while(idx.contains(c)); // avoid duplicates
				idx.add(c);

				// copy the value from _data[c]
				_centroids[i] = new double[_ndims];
				for (int j=0; j<_ndims; j++)
					_centroids[i][j] = _data[c][j];
			}
			System.out.println("selected random centroids");

		}

		double [][] c1 = _centroids;
		double threshold = 1;
		int round=0;

		while (true){
			// update _centroids with the last round results
			_centroids = c1;

			//assign record to the closest centroid
			_label = new int[_nrows];
			for (int i=0; i<_nrows; i++){
				_label[i] = closest(_data[i]);
			}

			// recompute centroids based on the assignments  
			c1 = updateCentroids();
			round ++;
			if ((niter >0 && round >=niter) || converge(_centroids, c1, threshold))
				break;
		}

		System.out.println("Clustering converges at round " + round);
	}

	// find the closest centroid for the record v 
	private int closest(double [] v){
		double mindist = dist(v, _centroids[0]);
		int label =0;
		for (int i=1; i<_numClusters; i++){
			double t = dist(v, _centroids[i]);
			if (mindist>t){
				mindist = t;
				label = i;
			}
		}
		return label;
	}

	// compute Euclidean distance between two vectors v1 and v2
	private double dist(double [] v1, double [] v2){
		double sum=0;
		for (int i=0; i<_ndims; i++){
			double d = v1[i]-v2[i];
			sum += d*d;
		}
		return Math.sqrt(sum);
	}

	// according to the cluster labels, recompute the centroids 
	// the centroid is updated by averaging its members in the cluster.
	// this only applies to Euclidean distance as the similarity measure.

	private double [][] updateCentroids(){
		// initialize centroids and set to 0
		double [][] newc = new double [_numClusters][]; //new centroids 
		int [] counts = new int[_numClusters]; // sizes of the clusters

		// intialize
		for (int i=0; i<_numClusters; i++){
			counts[i] =0;
			newc[i] = new double [_ndims];
			for (int j=0; j<_ndims; j++)
				newc[i][j] =0;
		}


		for (int i=0; i<_nrows; i++){
			int cn = _label[i]; // the cluster membership id for record i
			for (int j=0; j<_ndims; j++){
				newc[cn][j] += _data[i][j]; // update that centroid by adding the member data record
			}
			counts[cn]++;
		}

		// finally get the average
		for (int i=0; i< _numClusters; i++){
			for (int j=0; j<_ndims; j++){
				newc[i][j]/= counts[i];
			}
		} 

		return newc;
	}

	// check convergence condition
	// max{dist(c1[i], c2[i]), i=1..numClusters < threshold
	private boolean converge(double [][] c1, double [][] c2, double threshold){
		// c1 and c2 are two sets of centroids 
		double maxv = 0;
		for (int i=0; i< _numClusters; i++){
			double d= dist(c1[i], c2[i]);
			if (maxv<d)
				maxv = d;
		} 

		if (maxv <threshold)
			return true;
		else
			return false;

	}
	public double[][] getCentroids()
	{
		return _centroids;
	}

	public int [] getLabel()
	{
		return _label;
	}

	public int nrows(){
		return _nrows;
	}

	public Map<Long, Integer> takeResults(){

		Map<Long, Integer> mapUserCluster = new HashMap<>();
		System.out.println("Label:");
		for (int i=0; i<_nrows; i++)	{
			System.out.println(_label[i]);
			mapUserCluster.put(Long.valueOf(i), _label[i]);
		}
		System.out.println("Centroids:");
		for (int i=0; i<_numClusters; i++){
			for(int j=0; j<_ndims; j++)
				System.out.print(_centroids[i][j] + " ");

			System.out.println();
		}

		return mapUserCluster;
	}

	public static char[] checkDoppioUno(char[] array) {
		int i=0;
		for(int j=0; j<array.length; j++)	{
			if(array[j]=='1')
				i++;
			if(i>2)	{
				return array;
			}
		}
		if (i<2)	
			return array;
		return null;
	}
	
	public static double[][] getFirstCentroids()	{
		
		double[][] centroids = new double[55][10];
		
		int h=0;
		while(h<10)	{
			centroids[h][h] = 1.0;
			h++;
		}
		int f=10;

		while(f<55)	{
			for(int r=0; r<10; r++)	{
				for 	(int g=r+1; g<10; g++)	{
					centroids[f][r] =1.0;
					centroids[f][g] = 1.0;
					f++;
				}

			}
		}

//


		for (int l=0; l<55; l++){
			for(int m=0; m<10; m++)
				System.out.print(centroids[l][m] + " ");
			System.out.println();
		}
		
		return centroids;
	}




	public static Map<Long, Integer> clusterResult() throws PersistenceException {
		/**
		 * The code commented out here is just an example of how to use
		 * the provided functions and constructors.
		 * 
		 */

		List<User> users = UserPostgres.getAllUsers();

		int size = users.size();

		double[][] coordinates = new double[size][10];
		
		
		int i = 0;
		for (User user : users) {
			for (int j=0; j<user.getWeigths().length-1; j++) {

				coordinates[i][j] = user.getWeigth(j+1);
			}
			i++;
		}


		KMeans KM = new KMeans(coordinates,users.size(),10);
		double[][] centroids = KMeans.getFirstCentroids();
		KM.clustering(55, 1, centroids);
		Map<Long, Integer> mapUserCluster = KM.takeResults();
		
		return mapUserCluster;
	


	}
	
	
	
	
	
	
}