import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;

public class NaiveBayesClassifier {

    public static void main(String [] args) throws Exception {
        String line = null;
        String testingfilename = args[0];
        String trainingfilename = args[1];
        NaiveBayesTrainer trainer = new NaiveBayesTrainer();
        trainer.train(trainingfilename);
        
        //System.out.println(filename);
    }
}