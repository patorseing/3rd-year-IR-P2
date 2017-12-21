import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


/**
 * This class implements PageRank algorithm on simple graph structure.
 * Put your name(s), ID(s), and section here.
 *
 */
public class PageRanker {
	//I do:
	//Set variable
	Set<Integer> P;	//P is the set of all pages
	int N = 0;	//the numbers of all pages in P
	Set<Integer> S; //S is the set of sink nodes
	Map<Integer, Set<Integer>> M;  //M(p) is the set of pages that link to page p
	Map<Integer, Integer> L;	 //L(q) is the the number of out-links from page q 
	final double d = 0.85; //d is Static variables that project set d = 0.85
	Map<Integer, Double> PR;	//page rank collect p and score of p 
	
	
	int prevPerp = 0;	//it contain the previous Perplexity
	int iter = -1;	//converge Count
	
	List<Double> perp; //list all the Perplexity that is be calculated
	
	/**
	 * This class reads the direct graph stored in the file "inputLinkFilename" into memory.
	 * Each line in the input file should have the following format:
	 * <pid_1> <pid_2> <pid_3> .. <pid_n>
	 * 
	 * Where pid_1, pid_2, ..., pid_n are the page IDs of the page having links to page pid_1. 
	 * You can assume that a page ID is an integer.
	 */
	//I do:
	//load data from inputLinkFilename in M set(page, set of link to page)
	//if there are the duplicate page, the program will stop because the project did tell the solution to fix this problem
	public void loadData(String inputLinkFilename) {
		M = new HashMap<>();
		String line = null;
		try {
			BufferedReader pagelinkReader = new BufferedReader(new FileReader(new File(inputLinkFilename)));
			try {
				while ((line = pagelinkReader.readLine()) != null) {
					String[] tokens = line.split("\\W+");	//Break the words in the line
					int page = Integer.parseInt(tokens[0]);
					if (M.containsKey(page)) {	//check the page that is duplicate or not
	                    System.out.println("error: wrong format, the duplicate page");	//tell the program is error and it will be stop.
	                    return;
	                }
					Set linkin;	// the set that link to p that relate p in M set
					M.put(page, linkin = new TreeSet<>());
	                for (int i = 1; i < tokens.length; i++) {
	                		linkin.add(Integer.parseInt(tokens[i]));
	                }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will be called after the graph is loaded into the memory.
	 * This method initialize the parameters for the PageRank algorithm including
	 * setting an initial weight to each page.
	 */
	
	//I do:
	public void initialize(){
		P = new HashSet<>();	//the set of all pages
		N = M.size();	//the numbers of all pages in P
		
        S = new HashSet<>();	//the set of sink nodes
        L = new HashMap<>();	 //the the number of out-links from page q
        PR = new HashMap<>();	//page rank
        
        perp = new ArrayList<>();	//Perplexity list
        
        for (int page : M.keySet()) {	//initial q that start from not link to other yet
            L.put(page, 0);
        }
        
        for (int page : M.keySet()) {
            P.add(page);
            if (M.get(page).isEmpty()) {	//add sink node
                S.add(page);
            }
            for (int linkout : M.get(page)) {	//count the link that the page link out
            		if(L.get(linkout) == null) {
            			L.put(page, 0);
            		}
            		else {
            			L.put(linkout, L.get(linkout) + 1);
            		}
            }
            PR.put(page, 1.0 / N); 	// all page rank score must start from 1/N
        }
	}
	
	/**
	 * Computes the perplexity of the current state of the graph. The definition
	 * of perplexity is given in the project specs.
	 */
	
	//I do:
	public double getPerplexity(){
		double sum = 0;
		for(int p: P) {
			sum += PR.get(p) * (Math.log(PR.get(p))/Math.log(2));		//according to Formula that project give
		}
		double perplexity = Math.pow(2, -sum);	//according to Formula that project give
		perp.add(perplexity);	//keep all perplexities like a history of perplexity for find Converge
		return perplexity;
	}
	
	/**
	 * Returns true if the perplexity converges (hence, terminate the PageRank algorithm).
	 * Returns false otherwise (and PageRank algorithm continue to update the page scores). 
	 */
	
	//I do:
	public boolean isConverge(){
		int curr = (int) getPerplexity();//current perplexity
		iter++;	// count Converge than iteration
		if(iter == 0) {
			prevPerp = curr;
			return false;
		}
		if(prevPerp != curr) {
			iter = 0;
			prevPerp = curr;
			return false;
		}
		return iter >= 4 - 1;	// the page rank score should be Converge when check perplexity previous and current is same at least four iterations.
	}
	
	/**
	 * The main method of PageRank algorithm. 
	 * Can assume that initialize() has been called before this method is invoked.
	 * While the algorithm is being run, this method should keep track of the perplexity
	 * after each iteration. 
	 * 
	 * Once the algorithm terminates, the method generates two output files.
	 * [1]	"perplexityOutFilename" lists the perplexity after each iteration on each line. 
	 * 		The output should look something like:
	 *  	
	 *  	183811
	 *  	79669.9
	 *  	86267.7
	 *  	72260.4
	 *  	75132.4
	 *  
	 *  Where, for example,the 183811 is the perplexity after the first iteration.
	 *
	 * [2] "prOutFilename" prints out the score for each page after the algorithm terminate.
	 * 		The output should look something like:
	 * 		
	 * 		1	0.1235
	 * 		2	0.3542
	 * 		3 	0.236
	 * 		
	 * Where, for example, 0.1235 is the PageRank score of page 1.
	 * 
	 */
	
	//I do:
	//according to pseudo-code that project give 
	public void runPageRank(String perplexityOutFilename, String prOutFilename){
		Map<Integer, Double> newPR = new HashMap<>();
		while (!isConverge()) {	// in while 
            double sinkPR = 0.0;
            for (int sinkP : S) {
                sinkPR += PR.get(sinkP);
            }
            for (int page : P) {
                double newrank = (1 - d) / N;
                newrank += d * sinkPR / N;
                for (int linkout : M.get(page)) {
                		if(PR.get(linkout) == null) {
                			newrank = newrank;
                		}
                		else {
                			newrank += d * PR.get(linkout) / L.get(linkout);
                		}
                }
                newPR.put(page, newrank);
            }
            for (int page : P) {
                PR.put(page, newPR.get(page));
            }
        }
		BufferedWriter perplexity;
		BufferedWriter pagerank;
		try {
			perplexity = new BufferedWriter(new FileWriter(perplexityOutFilename));
			for(int i = perp.size()-4;i<perp.size();i++) {
				perplexity.write(perp.get(i) + "\n");
			}
			perplexity.close();
			pagerank = new BufferedWriter(new FileWriter(prOutFilename));
			for(int rank : PR.keySet()) {
				pagerank.write(rank + " " + PR.get(rank) + "\n");
			}
			pagerank.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the top K page IDs, whose scores are highest.
	 */
	
	//I do:
	public Integer[] getRankedPages(int K){
		Pair[] pageRank = new Pair[PR.size()];	//array pair that we create class pair for comparing page rank score in side PR set
		int i = 0;
		K = Math.min(K, pageRank.length);	//if the data is smaller than 100 page, the program should choose the size of data eg. test.dat
        for(int p: PR.keySet()){
        		pageRank[i++] = new Pair(p, PR.get(p));	//use the class Pair that we create to match page and page rank in array, pageRank 
        }
        
        Arrays.sort(pageRank);	

        Integer[] result = new Integer[K];
        for(i = 0; i < K; i++){
        		result[i] = pageRank[i].page;
        }

        return result;
	}
	
	//I do: that we use the pair from the previous project, YouGle to be inspired to create this class
		//and use Comparable to use compareTo to find the position that should be
		class Pair implements Comparable<Pair>{
	        int page;
	        double sroce;

	        public Pair(int page, double sroce) {
	            this.page = page;
	            this.sroce = sroce;
	        }

	        @Override
	        public int compareTo(Pair p) {
	            if(sroce > p.sroce){
	                return -1;	//the position should be before that page
	            }
	            if(sroce < p.sroce){
	                return 1;	//the position should be after that page
	            }
	            return 0;
	        }
	    }
	
	public static void main(String args[])
	{
	long startTime = System.currentTimeMillis();
		PageRanker pageRanker =  new PageRanker();
		pageRanker.loadData("citeseer.dat");
		//pageRanker.loadData("test.dat");
		pageRanker.initialize();
		pageRanker.runPageRank("perplexity.out", "pr_scores.out");
		Integer[] rankedPages = pageRanker.getRankedPages(100);
	double estimatedTime = (double)(System.currentTimeMillis() - startTime)/1000.0;
		
		System.out.println("Top 100 Pages are:\n"+Arrays.toString(rankedPages));
		System.out.println("Proccessing time: "+estimatedTime+" seconds");
	}
}
