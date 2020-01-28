import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifier implements Classifier {

	Map<Label, Integer> wordsCountPerLabel;
	Map<Label, Integer> documentsCountPerLabel;
	Map<String, Integer> positiveMap;
	Map<String, Integer> negativeMap;
	Integer vocabularySize;
	Integer trainDataSize;
    /**
     * Trains the classifier with the provided training data and vocabulary size
     */
    @Override
    public void train(List<Instance> trainData, int v) {
        // TODO : Implement
        // Hint: First, calculate the documents and words counts per label and store them. 
        // Then, for all the words in the documents of each label, count the number of occurrences of each word.
        // Save these information as you will need them to calculate the log probabilities later.
        //
        // e.g.
        // Assume m_map is the map that stores the occurrences per word for positive documents
        // m_map.get("catch") should return the number of "catch" es, in the documents labeled positive
        // m_map.get("asdasd") would return null, when the word has not appeared before.
        // Use m_map.put(word,1) to put the first count in.
        // Use m_map.replace(word, count+1) to update the value
    	trainDataSize = trainData.size();
    	vocabularySize = v;
    	wordsCountPerLabel = getWordsCountPerLabel(trainData);
    	documentsCountPerLabel = getDocumentsCountPerLabel(trainData);
    	positiveMap = new HashMap<String, Integer>();
    	negativeMap = new HashMap<String, Integer>();
    	for (Instance trainRow : trainData) {
    		if (trainRow.label == Label.POSITIVE) {
    			for (String word : trainRow.words) {
    				positiveMap.put(word, positiveMap.getOrDefault(word, 0) + 1);
    			}
    		} else {
    			for (String word : trainRow.words) {
    				negativeMap.put(word, negativeMap.getOrDefault(word, 0) + 1);
    			}
    		}
    	}
    }

    /*
     * Counts the number of words for each label
     */
    @Override
    public Map<Label, Integer> getWordsCountPerLabel(List<Instance> trainData) {
    	Map<Label, Integer> map = new HashMap<Label, Integer>();
        int pc = 0, nc = 0;
    	for (Instance trainRow : trainData) {
    		if (trainRow.label == Label.POSITIVE) {
    			pc += trainRow.words.size();
    		} else {
				nc += trainRow.words.size();
			}
    	}
    	map.put(Label.POSITIVE, pc);
        map.put(Label.NEGATIVE, nc);
        return map;
    }


    /*
     * Counts the total number of documents for each label
     */
    @Override
    public Map<Label, Integer> getDocumentsCountPerLabel(List<Instance> trainData) {
        Map<Label, Integer> map = new HashMap<Label, Integer>();
        int pc = 0, nc = 0;
    	for (Instance trainRow : trainData) {
    		if (trainRow.label == Label.POSITIVE) {
    			pc++;
    		} else {
				nc++;
			}
    	}
    	map.put(Label.POSITIVE, pc);
        map.put(Label.NEGATIVE, nc);
        return map;
    }


    /**
     * Returns the prior probability of the label parameter, i.e. P(POSITIVE) or P(NEGATIVE)
     */
    private double p_l(Label label) {
        // TODO : Implement
        // Calculate the probability for the label. No smoothing here.
        // Just the number of label counts divided by the number of documents.
    	if (trainDataSize == 0)
    		return 0;
    	return (double)documentsCountPerLabel.get(label)/trainDataSize;
    }

    /**
     * Returns the smoothed conditional probability of the word given the label, i.e. P(word|POSITIVE) or
     * P(word|NEGATIVE)
     */
    private double p_w_given_l(String word, Label label) {
        // TODO : Implement
        // Calculate the probability with Laplace smoothing for word in class(label)
    	int clw = (label == Label.POSITIVE) ? positiveMap.getOrDefault(word, 0) : negativeMap.getOrDefault(word, 0);
    	double numerator = clw + 1;
    	double denominator = vocabularySize + wordsCountPerLabel.get(label);
        return numerator / denominator;
    }

    /**
     * Classifies an array of words as either POSITIVE or NEGATIVE.
     */
    @Override
    public ClassifyResult classify(List<String> words) {
        // TODO : Implement
        // Sum up the log probabilities for each word in the input data, and the probability of the label
        // Set the label to the class with larger log probability
    	ClassifyResult result = new ClassifyResult();
    	double ps = 0, ns = 0;
    	for (String word : words) {
    		ps += Math.log(p_w_given_l(word, Label.POSITIVE));
    		ns += Math.log(p_w_given_l(word, Label.NEGATIVE));
    	}
    	result.logProbPerLabel = new HashMap<Label, Double>();
    	result.logProbPerLabel.put(Label.POSITIVE, Math.log(p_l(Label.POSITIVE)) + ps);
    	result.logProbPerLabel.put(Label.NEGATIVE, Math.log(p_l(Label.NEGATIVE)) + ns);
    	result.label = Label.POSITIVE;
    	if (result.logProbPerLabel.get(Label.NEGATIVE) > result.logProbPerLabel.get(Label.POSITIVE))
    		result.label = Label.NEGATIVE;
        return result;
    }


}
