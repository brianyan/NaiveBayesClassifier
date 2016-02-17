import java.util.*;
import java.io.*;

public class NaiveBayesTrainer{
	public static double posR = 0;
	public static double negR = 0;
	public HashMap<String, ArrayList<Double> > map = new HashMap<String, ArrayList<Double> >();
	
	public NaiveBayesTrainer(){

	}
	public void train(String filename) throws Exception{
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(filename));
        while((line = br.readLine()) != null){
        	// here we are processing the files
        	
        	line = normalize(line);
        	// String [] words = line.split("\\s");
        	Review review = new Review(line.split("\\s"));
        	for(int i=1; i< review.words.length; i++){
        		// if word not in hashmap
        		if(!map.containsKey(review.words[i])){
        			if(review.cat == Category.POS){
        				ArrayList<Double> temp = new ArrayList<Double>();
        				temp.add(1.0);
        				for(int n=1; n<4; n++){
        					temp.add(0.0);
        				}
        				map.put(review.words[i], temp);
               		}
               	else {
               		// neg category and not seen before
               		ArrayList<Double> temp = new ArrayList<Double>();
               		for(int n=0; n<4; n++){
        					temp.add(0.0);
        			}
        			temp.set(1, 1.0);
        			map.put(review.words[i], temp);
               	}
        	}
        	else { // we have seen the before
        		if(review.cat == Category.POS){
        			ArrayList<Double> temp = map.get(review.words[i]);
        			temp.set(0, temp.get(0) + 1);
        			map.put(review.words[i], temp);
        		}
        		else {
        			ArrayList<Double> temp = map.get(review.words[i]);
        			temp.set(1, temp.get(1) + 1);
        			map.put(review.words[i], temp);
        		}
        		

        	}
        }
        	if(review.cat == Category.POS) incrementpos(); else incrementneg();
        	// for(String s: map.keySet()){
        	// 	// getting all words
        	// 	ArrayList<Double> temp = map.get(s);
        	// 	double prob = w_given_C( map.get(s) , review.cat);

        	
        	// System.out.println(review.cat);
        	// System.out.println(line);
        }
        br.close();
        //System.out.println(getposR());
        for(String s: map.keySet()){
        		System.out.print(s + " count: ") ;
        		for(Double d: map.get(s))
        			System.out.print(d + " ");
        		System.out.println();
	}
	System.out.println(getposR());
	System.out.println(getnegR());
	System.out.println(this.probability_CategoryPos());
	}
	public String normalize(String s) {
		String [] Stopwords = {"?", "." , ",", "/", "!" };
		for(String c: Stopwords){
			s = s.replace(c, "");
		}	
		return s.toLowerCase();
	}
	public double probability_CategoryPos() {
		//prob is just #occurances/#total
			return (getposR() / totalR());
		//return (cat==Category.POS ? getposR() : getnegR() / (getposR()+ getnegR()) );
	}
	public double probability_CategoryNeg(){
		return getnegR() / totalR();
	}

	// public double proabilitywordgivencategory(String word, Category cat){

	// }

	public void incrementpos(){
		this.posR++;
	}
	public void incrementneg(){
		this.negR++;
	}
	public double getnegR() {
        return this.negR;
    }

    public double getposR() {
        return this.posR;
    }
    public double totalR(){
    	return this.negR + this.posR;
    }

}