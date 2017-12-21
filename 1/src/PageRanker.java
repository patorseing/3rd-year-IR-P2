import java.io.*;
import java.util.Arrays;
import java.util.*;

/**
 * This class implements PageRank algorithm on simple graph structure.
 * Put your name(s), ID(s), and section here.
 *
 */
public class PageRanker {
	
	/**
	 * This class reads the direct graph stored in the file "inputLinkFilename" into memory.
	 * Each line in the input file should have the following format:
	 * <pid_1> <pid_2> <pid_3> .. <pid_n>
	 * 
	 * Where pid_1, pid_2, ..., pid_n are the page IDs of the page having links to page pid_1. 
	 * You can assume that a page ID is an integer.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	Set<Integer> P;
	Set<Integer> S;
	Map<Integer, Set<Integer>> M;
	Map<Integer, Integer> L;
	Map<Integer, Double> PR;
	final double d = 0.85;
	int N = 0;
	int lastPerplexity = 0;
	int converge = -1;	//count converge
	List<Double> perplexity;	//perplexity history
	
	public void loadData(String inputLinkFilename){
		M = new HashMap<>();
		try {
			FileReader data = new FileReader(inputLinkFilename);
			Scanner input = new Scanner(data);
			while(input.hasNextLine()) {
				String line = input.nextLine().trim();
				if(line.isEmpty()) {
					continue;
				}
				//String[] tokens = line.split("\t");
				String[] tokens = line.split("\\W+");
				if(M.containsKey(tokens[0])) {
					System.out.println("Wrong format");
					return;
				}
				int p = Integer.parseInt(tokens[0]);
				Set tmp;		//temp
				M.put(p, tmp = new TreeSet<>());
				for(int i = 1; i< tokens.length;i++) {
					tmp.add(Integer.parseInt(tokens[i]));
					
				}
			}
			//test
			for(int m: M.keySet()) {
				System.out.println(m +" num out "+ M.get(m));
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
	public void initialize(){
		P = new HashSet<>();
		S = new HashSet<>();
		L = new HashMap<>();
		PR = new HashMap<>();
		perplexity = new ArrayList<>();
		N = M.size();
		for(int node: M.keySet()) {
			L.put(node, 0);
		}
		for(int node: M.keySet()) {
			P.add(node);
			if(M.get(node).isEmpty()) {
				S.add(node);
			}
			for(int q: M.get(node)) {	//q = countLinkOut
				L.put(q, L.get(q)+1);
			}
			PR.put(node, 1.0/N);
		}
		test();
	}
	
	void test() {
		//test 
		System.out.println("p :" + P);
		System.out.println("s :" + S);
		System.out.println("l :");
		for(int l: L.keySet()) {
			System.out.println(l +" num out "+ L.get(l));
		}
		testPR();
	}
	void testPR(){
		System.out.println("pr :");
		for(int pr: PR.keySet()) {
			System.out.println(pr +" has rank "+ PR.get(pr));
		}
	}
	/**
	 * Computes the perplexity of the current state of the graph. The definition
	 * of perplexity is given in the project specs.
	 */
	public double getPerplexity(){
		//Math.log(x)/Math.log(2) == log2(x)
		double sum = PR.values().stream().map(x -> x * Math.log(x)/Math.log(2)).mapToDouble(Double::doubleValue).sum();
		double p = Math.pow(2, -sum);
		perplexity.add(p);
		return p;
	}
	
	/**
	 * Returns true if the perplexity converges (hence, terminate the PageRank algorithm).
	 * Returns false otherwise (and PageRank algorithm continue to update the page scores). 
	 */
	public boolean isConverge(){
		int curr = (int) getPerplexity();//current perplexity
		converge++;	// count Converge
		if(converge == 0) {
			lastPerplexity = curr;
			return false;
		}
		if(lastPerplexity != curr) {
			converge = 0;
			lastPerplexity = curr;
			return false;
		}
		return converge >= 4 - 1;
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
	public void runPageRank(String perplexityOutFilename, String prOutFilename){
		Map<Integer, Double> nPR = new HashMap<>(); // new page rank
		while(!isConverge()) {
			double sinkPR = 0;
			for(int Sp: S) {
				sinkPR += PR.get(Sp);
			}
			for(int p: P) {
				double cal = 1 - d / N;	//calculating
				cal += d*sinkPR/N;
				for(int Mp: M.get(p)) {
					cal += d* PR.get(Mp) / L.get(Mp);
				}
				nPR.put(p, cal);
			}
			for(int p: P) {
				PR.put(p, nPR.get(p));
			}
		}
		try {
			PrintWriter perplexityOut = new PrintWriter(perplexityOutFilename);
			PrintWriter prOut = new PrintWriter(prOutFilename);
			for(int i = perplexity.size()-4;i<perplexity.size();i++) {
				perplexityOut.println(perplexity.get(i));
			}
			for(int rank : PR.keySet()) {
				prOut.println(rank + " " + PR.get(rank));
			}
			perplexityOut.close();
			prOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the top K page IDs, whose scores are highest.
	 */
	public Integer[] getRankedPages(int K){return null;}
	
	public static void main(String args[])
	{
	long startTime = System.currentTimeMillis();
		PageRanker pageRanker =  new PageRanker();
		//pageRanker.loadData("citeseer.dat");
		pageRanker.loadData("test.dat");
		pageRanker.initialize();
		pageRanker.runPageRank("perplexity.out", "pr_scores.out");
		Integer[] rankedPages = pageRanker.getRankedPages(100);
	double estimatedTime = (double)(System.currentTimeMillis() - startTime)/1000.0;
		System.out.println("Top 100 Pages are:\n"+Arrays.toString(rankedPages));
		System.out.println("Proccessing time: "+estimatedTime+" seconds");
	}
}
