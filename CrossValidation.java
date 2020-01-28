import java.util.ArrayList;
import java.util.List;

public class CrossValidation {
	/*
     * Returns the k-fold cross validation score of classifier clf on training data.
     */
    public static double kFoldScore(Classifier clf, List<Instance> trainData, int k, int v) {
        int i, foldSize = trainData.size()/k;
        double accuracy = 0;
        for (i=0; i<k; i++) {
        	List<Instance> testSetInstances = new ArrayList<Instance>();
        	for(int j=0; j<foldSize; j++) {
    			testSetInstances.add(trainData.get(0));
    			trainData.remove(0);
        	}
        	clf.train(trainData, v);
        	accuracy += test(clf, testSetInstances);
        	//System.out.println(i + "th fold, acc: ", accuracy);
        	for(Instance testRow : testSetInstances) {
        		trainData.add(testRow);
        	}
        }
        return accuracy/k;
    }
    
    static double test(Classifier clf, List<Instance> testSet) {
    	double c = 0;
    	for(Instance testRow : testSet) {
    		ClassifyResult result = clf.classify(testRow.words);
    		if(result.label == testRow.label)
    			c++;
    	}
    	return (c/testSet.size());
    }
}
