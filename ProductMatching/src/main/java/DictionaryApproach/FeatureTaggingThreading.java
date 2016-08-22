package DictionaryApproach;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import BagOfWordsModel.ModelConfiguration;
import BagOfWordsModel.PreprocessingConfiguration;

public class FeatureTaggingThreading {
	
	HashMap<String, HashMap<Integer, List<String>>> tokenizedInput_;
	ModelConfiguration model_;
	String htmlFolder_;
	String htmlParsingElements_;
	PreprocessingConfiguration preprocessing_;
	Dictionary dictionary_;
	HashMap<String,List<String>> tokensOfAllHTML=new HashMap<String,List<String>>();
	Thread t;
	int numberOfThreads_;


	
	public FeatureTaggingThreading(HashMap<String, HashMap<Integer, List<String>>> tokenizedInput, ModelConfiguration model, 
			String htmlFolder, String htmlParsingElements, Dictionary dictionary,
			PreprocessingConfiguration preprocessing, int numberOfThreads) throws IOException {
		tokenizedInput_=tokenizedInput;
		model_=model;
		htmlFolder_=htmlFolder;
		htmlParsingElements_=htmlParsingElements;
		dictionary_=dictionary;
		preprocessing_=preprocessing;
		numberOfThreads_=numberOfThreads;

	}
	
//	public HashMap<String,List<String>> getFeatureTagging() throws IOException, InterruptedException{
//		
//		File folderHTML = new File(htmlFolder_);
//		File[] listOfHTML = folderHTML.listFiles();
//		
//		
//		System.out.println("Begin Feature Tagging");
//		Thread[] threads = new Thread[numberOfThreads_];
//		int th=0;
//		for (int i = 0; i < listOfHTML.length; i++) {
//			
//			t= new Thread(new FeatureTag(listOfHTML[i]));
//			t.start();
//			threads[th]=t;
//			th++;
//			
//			if(i==listOfHTML.length-1) {
//				for(int c=0;c<numberOfThreads_;c++){
//					if(null!=threads[c])
//						threads[c].join();
//				}
//				System.out.println("Finished with tagging");
//				continue;
//			}
//			
//			else if(null!=threads[numberOfThreads_-1]){
//				th=0;
//				for(int c=0;c<numberOfThreads_;c++){
//				threads[c].join();
//				System.out.println(i+1+" out of "+listOfHTML.length+" DONE");
//				}
//				threads= new Thread[numberOfThreads_];
//			}
//			
//		}
//		
//		writer_.close();
//		return tokensOfAllHTML;
//	}
	
	public HashMap<String,List<String>> getFeatureTagging() throws IOException, InterruptedException{
		
		File folderHTML = new File(htmlFolder_);
		File[] listOfHTML = folderHTML.listFiles();
		
		
		System.out.println("Begin Feature Tagging");
        ExecutorService executor = Executors.newFixedThreadPool(100);

		
		for (int i = 0; i < listOfHTML.length; i++) {
			Runnable worker = new FeatureTag(listOfHTML[i], dictionary_,preprocessing_,model_);
			executor.execute(worker);			
		}
		executor.shutdown();
		while(!executor.isTerminated()){
			
		}
		System.out.println("Finished all threads");
		
		return tokensOfAllHTML;
	}
	
	public class FeatureTag implements Runnable{
		
		private File html_;
		FeatureTagger tag;
		FeatureTaggerResult ftResult;
		HashMap <String, ArrayList<String>> tagged;
		HashMap <String, ArrayList<String>> reversed;
		private Dictionary dict;
		private PreprocessingConfiguration pr;
		private ModelConfiguration m;
		

		FeatureTag(File htmlFile, Dictionary dict, PreprocessingConfiguration pr, ModelConfiguration m) throws IOException {
			html_=htmlFile;
			this.dict=dict;
			this.pr=pr;
			this.m=m;
			
		}

		@Override
		public void run(){
			try{
				
				tag=new FeatureTagger(tokenizedInput_.get(html_.getName()));
				ftResult = tag.setFeatureTagging(html_.getPath(),dict.getDictionary(),pr,m);
				
				List<String> allTaggedTokens= new ArrayList<String>();
		    	
				//do the tagging step
				
				tagged =  ftResult.getTaggedWords();
				reversed = tag.reverseTaggedWords(tagged);
				for(Map.Entry<String, ArrayList<String>> r: reversed.entrySet()){
					for(String value:r.getValue()){
						//add it as many times as it appeared in the corpus
						for(int fr=0;fr<ftResult.getTaggedWordFrequency().get(value);fr++)
							allTaggedTokens.add(value);
					}			
				}
				
				if (reversed.size()==0) {
					System.out.println("No tagging could be done for the page:"+html_.getPath());
				}
				//in order to get the idf  based on tokenized words - but if we do the tagging based on groups of tokens we should not make it like this
				//tokensOfAllHTML.put(listOfHTML[i].getName(), getTokenizedTaggedWords(reversed));
				tokensOfAllHTML.put(html_.getName(), allTaggedTokens);

			}
			catch(Exception e){
				e.printStackTrace();
				System.exit(0);
	
			}
		
		}
	}
}
