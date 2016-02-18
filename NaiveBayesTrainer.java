import java.util.*;
import java.io.*;

public class NaiveBayesTrainer{
	public static double posR = 0;
	public static double negR = 0;
	public static int TotalWordsInCatPos = 0;
	public static int TotalWordsInCatNeg = 0;
	public HashMap<String, ArrayList<Double> > map = new HashMap<String, ArrayList<Double> >();
	// Array List is 0 index -> positive word count , 1 index -> negative word count
	// 2 index -> 
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
        		if(review.cat == Category.POS)
        			incrementWordsInCatPos();
        		else
        			incrementWordsInCatNeg();
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
        	ArrayList<Double> temp = map.get(s);
        	temp.set(2,this.WordGivenCategory(s,Category.POS));
        	temp.set(3,this.WordGivenCategory(s,Category.NEG));
        	// //get probrability of word (word given cat neg)
        	// // and word given cat pos
        	// int positivewords = temp.get(0);
        	// int negativewords = temp.get(1);
        	// int vocab = getVocabulary();
        	// int totalnumberwordsPos = this.getTotalWordsinCatPos();
        	// int totalnumberwordsNeg = this.getTotalWordsinCatNeg();
        	// double prob 
        	// temp.set(2)
        }


        for(String s: map.keySet()){
        		System.out.print(s + " count: ") ;
        		for(Double d: map.get(s))
        			System.out.print(d + " ");
        		System.out.println();
	}
	System.out.println(getposR());
	System.out.println(getnegR());
	System.out.println(this.probability_CategoryPos());
	System.out.println(this.TotalWordsInCatPos);
	System.out.println(this.TotalWordsInCatNeg);
	System.out.println(getVocabulary());

	}
	public void classify(String filename) throws Exception{
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(filename));
        while((line = br.readLine()) != null){




        }
	}
	public String normalize(String s) {
		String [] Stopwords = {"?", "." , ",", "/", "!" , "("  ,")"
		, "'", "-", "\"", ">", "<"};
		for(String c: Stopwords){
			s = s.replace(c, " ");
		}	
		return s.toLowerCase();
	}

	public double WordGivenCategory(String word, Category cat) {
		int c = (cat == Category.POS) ? 1 : 0;
		int totalwords = 0;
		double numOfThisCat = 0;
		if(c == 1) 
			totalwords = getTotalWordsinCatPos();
		else
			totalwords = getTotalWordsinCatNeg();
		//if the word is in the map, use that data
		if(map.containsKey(word)){
			if(c == 1)
				numOfThisCat = map.get(word).get(0); //get count of occurences that this word appears in pos category
			else
				numOfThisCat = map.get(word).get(1); //get count of occurences that this word appears in neg category
		}
		//otherwise use 0
		else{
			numOfThisCat = 0;
		}
		double smooth = 1;
		return ((numOfThisCat+smooth)/(totalwords + getVocabulary() ));
	}
	/* returns the total number of words in the set */
	public int getVocabulary(){
		return this.map.size();
	}

	// public double calculate(String keyWord, String category) {
	// 	if (probCache.contains(keyWord, category)) {
	// 		return probCache.get(keyWord, category);
	// 	}
	// 	Integer Nxc = tdm.numberOfWord(keyWord, category);
	// 	Integer Nc = tdm.totalNumberOfWords(category);
	// 	Integer V = tdm.getVocabulary();

	// 	Double prob = (Nxc + 1) / (Nc + M + V);
	// 	probCache.add(keyWord, category, prob);

	// 	return prob;
	// }
	public double probability_CategoryPos() {
		//prob is just #occurances/#total
			return (getposR() / totalR());
		//return (cat==Category.POS ? getposR() : getnegR() / (getposR()+ getnegR()) );
	}
	public double probability_CategoryNeg(){
		return getnegR() / totalR();
	}

	public void incrementpos(){
		this.posR++;
	}
	public void incrementneg(){
		this.negR++;
	}
	public void incrementWordsInCatPos(){
		this.TotalWordsInCatPos++;
	}
	public void incrementWordsInCatNeg(){
		this.TotalWordsInCatNeg++;
	}
	public int getTotalWordsinCatPos(){
		return this.TotalWordsInCatPos;
	}
	public int getTotalWordsinCatNeg(){
		return this.TotalWordsInCatNeg;
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