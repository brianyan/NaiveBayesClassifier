import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class NaiveBayesTrainer{
	DecimalFormat df = new DecimalFormat("#.###");
	public double startTimeLabeling = 0.0;
	public double endTimeLabeling = 0.0;
	public double startTimeTraining = 0.0;
	public double endTimeTraining = 0.0;
	public double durationtesting = 0.0;
	public double durationtraining = 0;
	public static double posR = 0;
	public static double negR = 0;
	public static int TotalWordsInCatPos = 0;
	public static int TotalWordsInCatNeg = 0;
	public double MatchTotalLabeling = 0;
	public double NonMatchTotalLabeling = 0;
	public double MatchTotalTrainingLabeling = 0;
	public double NonMatchTotalTrainingLabeling = 0;
	public HashMap<String, ArrayList<Double> > map = new HashMap<String, ArrayList<Double> >();
	// Array List is 0 index -> positive word count , 1 index -> negative word count
	// 2 index -> 
	public NaiveBayesTrainer(){

	}
	public void train(String filename) throws Exception{
		this.startTimeTraining = System.currentTimeMillis();
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(filename));
        while((line = br.readLine()) != null){
        	// here we are processing the files
        	
        	line = normalize(line);
        	// String [] words = line.split("\\s");
        	Review review = new Review(line.split("\\s"));
        	for(int i=1; i< review.words.length; i++){
        		// if word not in hashmap

        		String s = review.words[i];
        		if(s.matches("[0-9]+"))
        			continue;
			
        		if(s.length() >= 4){
	        		if(s.substring(s.length()-3,s.length()).equals("ing")){
	        			s = s.substring(0, s.length()-3);
	        		} 
        		}	
        		if(review.cat == Category.POS)
        			incrementWordsInCatPos();
        		else
        			incrementWordsInCatNeg();
        		if(!map.containsKey(s)){
        			if(review.cat == Category.POS){
        				ArrayList<Double> temp = new ArrayList<Double>();
        				temp.add(1.0);
        				for(int n=1; n<4; n++){
        					temp.add(0.0);
        				}
        				map.put(s, temp);
               		}
               	else {
               		// neg category and not seen before
               		ArrayList<Double> temp = new ArrayList<Double>();
               		for(int n=0; n<4; n++){
        					temp.add(0.0);
        			}
        			temp.set(1, 1.0);
        			map.put(s, temp);
               	}
        	}
        	else { // we have seen the before
        		if(review.cat == Category.POS){
        			ArrayList<Double> temp = map.get(s);
        			temp.set(0, temp.get(0) + 1);
        			map.put(s, temp);
        		}
        		else {
        			ArrayList<Double> temp = map.get(s);
        			temp.set(1, temp.get(1) + 1);
        			map.put(s, temp);
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
        System.out.println(getposR());
        for(Iterator<Map.Entry<String, ArrayList<Double>>> it = map.entrySet().iterator(); it.hasNext(); ) {
	      Map.Entry<String, ArrayList<Double>> entry = it.next();
	      if( (entry.getValue().get(0) <= 1 && entry.getValue().get(1) <=1) ||
	      	 (Math.abs(entry.getValue().get(0) - entry.getValue().get(1)) <1) ) {
	        it.remove();
	      }
	    }
        for(String s: map.keySet()){
        	ArrayList<Double> temp = map.get(s);
        	temp.set(2,this.WordGivenCategory(s,Category.POS));
        	temp.set(3,this.WordGivenCategory(s,Category.NEG));
        	// if(map.get(s).get(0) <= 1 && map.get(s).get(1) <=1){
        	// 	map.remove(s);
        	// }
        }


        for(String s: map.keySet()){
        		System.out.print(s + " count: ") ;
        		for(Double d: map.get(s))
        			System.out.print(d + " ");
        		System.out.println();
	}
    endTimeTraining = System.currentTimeMillis();
	durationtraining = (endTimeTraining - startTimeTraining) * .001;
	}
	public void classifyTesting(String filename) throws Exception{
		// This should display the testing results in a nice format
		String line = null;
		this.startTimeLabeling = System.currentTimeMillis();
		BufferedReader br = new BufferedReader(new FileReader(filename));
        while((line = br.readLine()) != null){
        	Review review = new Review(line.split("\\s"));
        	Category outcome = this.classifyReview(review);
        	if(outcome == Category.POS)
        		System.out.println(1);
        	else
        		System.out.println(0);
        	if(outcome == review.cat){
        		MatchTotalLabeling++;//System.out.println("Match");
        	}
        	else 
        		NonMatchTotalLabeling++; //System.out.println("No Match");

        }
        br.close();
        endTimeLabeling = System.currentTimeMillis();
        durationtesting = (endTimeLabeling - startTimeLabeling) * .001;
        int durationtrainingint = (int) durationtraining;
        int durationtestingint = (int) durationtesting;
        System.out.println(durationtrainingint + " seconds" + " (training)");
        System.out.println(durationtestingint + " seconds (labeling)");
        // System.out.println(df.format( (MatchTotalTrainingLabeling) / (NonMatchTotalTrainingLabeling + MatchTotalTrainingLabeling) ) + " (training)");
        System.out.println( df.format( (MatchTotalTrainingLabeling / (NonMatchTotalTrainingLabeling + MatchTotalTrainingLabeling)) ) + " (training)");
        System.out.println(df.format( (MatchTotalLabeling) / (NonMatchTotalLabeling + MatchTotalLabeling) ) + " (testing)");
        
        //System.out.println(matchtotal);
       // System.out.println(nonmatchtotal);
	}

	public void classifyTraining(String filename) throws Exception{
		// This should display the testing results in a nice format
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(filename));
        while((line = br.readLine()) != null){
        	Review review = new Review(line.split("\\s"));
        	Category outcome = this.classifyReview(review);
        	if(outcome == review.cat){
        		MatchTotalTrainingLabeling++;//System.out.println("Match");
        	}
        	else 
        		NonMatchTotalTrainingLabeling++; //System.out.println("No Match");

        }
        br.close();
        // endTimeLabeling = System.currentTimeMillis();
        // durationtesting = (endTimeLabeling - startTimeLabeling) * .001;
        // System.out.println(df.format(durationtraining) + " seconds" + " (training)");
        // System.out.println(df.format(durationtesting) + " seconds (labeling)");
        // // System.out.println(df.format( (MatchTotalTrainingLabeling) / (NonMatchTotalTrainingLabeling + MatchTotalTrainingLabeling) ) + " (training)");
        // System.out.println(NonMatchTotalTrainingLabeling);
        // System.out.println(df.format( (MatchTotalLabeling) / (NonMatchTotalLabeling + MatchTotalLabeling) ) + " (testing)");
        
        //System.out.println(matchtotal);
       // System.out.println(nonmatchtotal);
	}
	/* method to clasify one review as POS or NEG */
	public Category classifyReview(Review r) {
		// review should be a String [] with 0 index being its actualy review outcome
		// should compute probabilities and output 1 if review is POS 
		// output 0 if review is NEG
		double CatPos = Math.log(probability_CategoryPos());
		double CatNeg = Math.log(probability_CategoryNeg());
		double ProbWords_in_Neg = 0.0;
		double ProbWords_in_Pos = 0.0;
		for(int i=1; i< r.words.length; i++){
			ProbWords_in_Pos += Math.log(this.WordGivenCategory(r.words[i], Category.POS));
			ProbWords_in_Neg += Math.log(this.WordGivenCategory(r.words[i], Category.NEG));
		}
		double finalCatPosSum = CatPos + ProbWords_in_Pos;
		double finalCatNegSum = CatNeg + ProbWords_in_Neg;

		//System.out.println("This is Pos :" + finalCatPosSum);
		//System.out.println("This is Neg:" + finalCatNegSum);
		if(finalCatPosSum>finalCatNegSum)
			return Category.POS;
		return Category.NEG;

	}


	public String normalize(String s) {
		String [] Stopwords = {"?", "." , ",", "/", "!" , "("  ,")"
		, "'", "-", "\"", ">", "<", "the" , ":", ";" , "case", "ago", "he",
		 "she", "all", "speak", "maybe",};
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