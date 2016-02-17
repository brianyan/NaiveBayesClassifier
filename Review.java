public class Review {
	public Category cat;
	public String [] words;

	public Review(String[] words){
		this.words = words;
		if(this.words[0].equals("1")){
			this.cat = Category.POS;
		}
		else {
			this.cat = Category.NEG;
		}
	}
}