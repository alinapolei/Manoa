package sample;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;



public class Indexer {

    /**
     * the main dictionary
     * the key is the term and the value is instance of DicEntery
     * contains the data: term, df, tfCorpus
     */
    private Map<String, DicEntry> dic;
    /**
     * the temp dictionary for words with lower letters
     * the key is the term and the value is instance of DicEntery
     * contains the data: term, df, tfCorpus
     */
    private Map<String, DicEntry> lowTerms;
    /**
     * the temp dictionary for words with upper letters
     * the key is the term and the value is instance of DicEntery
     * contains the data: term, df, tfCorpus
     */
    private Map<String, DicEntry> upTerms;
    /**
     * the temp dictionary for words that only starts with upper letter
     * the key is the term and the value is instance of DicEntery
     * contains the data: term, df, tfCorpus
     */
    private Map<String, DicEntry> mixTerms;
    /**
     * the temp posting
     * the key is the term and the value is instance of PostEntery
     * contains the data: doc name, tf, is represents a term that has an instance in the document's title
     */
    private Map<String, HashMap<String, PostEntry>> tmpPosting;

    /**
     * constructor for indexer
     */
    public Indexer() {
        dic = Collections.synchronizedMap(new HashMap<>());
        lowTerms = Collections.synchronizedMap(new HashMap<>());
        upTerms = Collections.synchronizedMap(new HashMap<>());
        mixTerms = Collections.synchronizedMap(new HashMap<>());
        tmpPosting = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * adds the term to the dictionary
     * @param term
     * @param doc - doc name
     * @param isTitle - if the term is in the title
     */
    public void setDic(String term,Doc doc, boolean isTitle) {
        if (!this.dic.containsKey(term)) {
            newTerm(term, doc, this.dic, isTitle);
            doc.increaseNumOfWords();
            if(doc.getMaxtf() == 0)
                doc.setMaxtf(1);
        }
        else{
            this.dic.get(term).setTfCourpus( this.dic.get(term).getTfCourpus()+1);
            editTerm(term, doc, this.dic, isTitle);
        }
    }

    /**
     * adds the term to the temp dictionary with lower words
     * @param term
     * @param doc - doc name
     * @param isTitle - if the term is in the title
     */
    public void setLowerTerms(String term,Doc doc, boolean isTitle) {
        if (!this.lowTerms.containsKey(term))
            newTerm(term, doc, this.lowTerms, isTitle);
        else{
            int x=this.lowTerms.get(term).getTfCourpus()+1;
            this.lowTerms.get(term).setTfCourpus(x) ;
            editTerm(term, doc, this.lowTerms, isTitle);
        }
    }

    /**
     * adds the term to the temp dictionary with the upper words
     * @param term
     * @param doc - doc name
     * @param isTitle - if the term is in the title
     */
    public void setUpperTerms(String term,Doc doc, boolean isTitle) {
        if (!this.upTerms.containsKey(term))
            newTerm(term, doc, this.upTerms, isTitle);
        else{
            this.upTerms.get(term).setTfCourpus( this.upTerms.get(term).getTfCourpus()+1);
            editTerm(term, doc, this.upTerms, isTitle);
        }
    }

    /**
     * adds the term to the temp dictionary with words that only the first letter is upper case
     * @param term
     * @param doc - doc name
     * @param isTitle - if the term is in the title
     */
    public void setMixedTerms(String term,Doc doc, boolean isTitle) {
        if (!this.mixTerms.containsKey(term))
            newTerm(term, doc, this.mixTerms, isTitle);
        else{
            this.mixTerms.get(term).setTfCourpus( this.mixTerms.get(term).getTfCourpus()+1);
            editTerm(term, doc, this.mixTerms, isTitle);
        }
    }

    /**
     * treats the case when the term is new to each of the dictionaries
     * builds a new object of DicEntery and appropriate object of PostEntery
     * @param term
     * @param doc
     * @param terms - the specific dictionary that the term needs to be added
     * @param isTitle
     */
    private void newTerm(String term, Doc doc, Map<String, DicEntry> terms, boolean isTitle) {
        DicEntry dicEntry=new DicEntry(term);
        terms.put(term, dicEntry);
        HashMap<String, PostEntry> linkedList = new LinkedHashMap<>();
        linkedList.put(doc.getDocNumber(), new PostEntry(doc.getDocNumber(), isTitle));
        tmpPosting.put(term, linkedList);
    }

    /**
     * treats the case when the term is already in the dictionary
     * no need to be added again, only need to build an appropriate object of PostEntery if necessary
     * @param term
     * @param doc
     * @param terms - the specific dictionary that the term needs to be added
     * @param isTitle
     */
    private void editTerm(String term, Doc doc, Map<String, DicEntry> terms, boolean isTitle) {
        HashMap<String, PostEntry> postEntries = tmpPosting.get(term);
        if(postEntries==null) {
            HashMap<String, PostEntry> linkedList = new LinkedHashMap<>();
            linkedList.put(doc.getDocNumber(), new PostEntry(doc.getDocNumber(), isTitle));
            tmpPosting.put(term, linkedList);
            //doc.increaseNumOfWords();
            //if(doc.getMaxtf() == 0)
            //doc.setMaxtf(1);
        }
        else{
            PostEntry post = postEntries.get(doc.getDocNumber());//conInPosting(doc.getDocNumber(), term);
            if (post != null) {
                post.increaseTf();
                if(!post.isTitle() && isTitle)
                    post.setTitle(true);
                if(terms == dic)
                    if(doc.getMaxtf()<post.getTf())
                        doc.setMaxtf(post.getTf());
            }
            else {
                postEntries.put(doc.getDocNumber(), new PostEntry(doc.getDocNumber(), isTitle));
                DicEntry de = terms.get(term);
                de.setDf(de.getDf()+1);
                if(terms == dic) {
                    if (doc.getMaxtf() == 0)
                        doc.setMaxtf(1);
                    doc.increaseNumOfWords();
                }
            }
        }
    }

    public Map<String, DicEntry> getDic() {
        return dic;
    }

    /**
     * goes throw the tmp posting and removes the stop words
     * @param stopWords - set of the stop words
     */
    public void removeStopWordfromtmpPosting( Set<String> stopWords) {
        for (int i = 0; i < stopWords.size(); i++) {
            Main.indexer.getTmpPosting().remove(stopWords.toArray()[i].toString().toLowerCase());
            Main.indexer.getTmpPosting().remove(stopWords.toArray()[i].toString().toUpperCase());
            String tmp = Character.toUpperCase((stopWords.toArray()[i]).toString().toCharArray()[0]) + (stopWords.toArray()[i]).toString().substring(1).toLowerCase();
            Main.indexer.getTmpPosting().remove(tmp);
        }
    }

    /**
     * goes throw the the dictionaries and removes the stop words
     * @param stopWords - set of the stop words
     */
    public void removeStopWords(Set<String> stopWords) {
        for (int i = 0; i < stopWords.size(); i++) {
            Main.indexer.getDic().remove(stopWords.toArray()[i]);
            String tmp = Character.toUpperCase((stopWords.toArray()[i]).toString().toCharArray()[0]) + (stopWords.toArray()[i]).toString().substring(1).toLowerCase();
            Main.indexer.getDic().remove(tmp);
            Main.indexer.getMixTerms().remove(tmp);
            Main.indexer.getUpTerms().remove(stopWords.toArray()[i].toString().toUpperCase());
            Main.indexer.getLowTerms().remove(stopWords.toArray()[i].toString().toLowerCase());
        }
    }

    /**
     * writes the data from tmp posting to the dick
     * this function is called when the chunk is full
     * at the end of this function the tmp posting is empty
     * @param path - the destination path to write the data
     * @throws IOException
     */
    public void transferToDisk(String path) throws IOException {
        removeStopWordfromtmpPosting(Main.stopWords);
        removeStopWords(Main.stopWords);
        setAllTerms();
        System.out.println("[+]Finish setall Terms");
        HashMap<String, PostEntry> list;
        File file;
        ArrayList<String> sortList=sortTempPosting();
        Map<Character, ArrayList<String>>Finalsort=finishSort(sortList);
        PrintWriter out=null;
        FileWriter fos=null;
        List<String> list1;
        for(Character x : Finalsort.keySet()) {
                Character character = x;
                if (!Character.isDigit(x.charValue()) && !Character.isLetter(x.charValue()) && character != '$')
                    file = new File(path + "\\" + "rest" + ".txt");
                else
                    file = new File(path + "\\" + x + ".txt");

                if(file.exists()) {
                    list1 = Files.readAllLines(Paths.get((file.getPath())));
                }
                else
                   list1 =new ArrayList<>();
                sortList.clear();
                sortList = Finalsort.get(x);
                for (String term : sortList) {
                    list = tmpPosting.get(term);
                    StringBuilder t=new StringBuilder();
                    for (PostEntry post : list.values())
                        t.append(post.toString()+", ");
                    list1.add(term + " ->" + t);
            }
            System.gc();
            file.delete();
             fos = new FileWriter(file, true);
            out = new PrintWriter(fos, true);

            for( String a :list1)
                     out.println(a);
                    out.close();
                    //System.gc();
                    list1.clear();
            /*
            if (out!=null)
                fos.flush();
                out.close();
                out = null;
                System.gc();
                fos=null;
                */
                }
        tmpPosting.clear();
    }

    /**
     * sorts the tmp posting by the term key according to alpha beth
     * @return a sorted ArrayList<String>
     */
    private ArrayList<String> sortTempPosting() {
        ArrayList<String> sortedKeys =
                new ArrayList<String>(tmpPosting.keySet());
        Collections.sort(sortedKeys);
        return sortedKeys;
    }

    /**
     *
     * @param sortList
     * @return a map that consists of
     * the key - first character of the letter
     * and the value - all the terms that starts with this character
     */
    private Map<Character, ArrayList<String>> finishSort(ArrayList<String>sortList){
        Map <Character,ArrayList<String>> MapAlpha=new HashMap<>();
        for(String x :sortList) {
            if (x.compareTo("") != 0) {
                Character tmp = Character.toLowerCase(x.toCharArray()[0]);
                if (!MapAlpha.containsKey(tmp)) {
                    MapAlpha.put(tmp, new ArrayList<>());
                    MapAlpha.get(tmp).add(x);
                } else
                    MapAlpha.get(tmp).add(x);
            }
        }
        return MapAlpha;
    }
    public Map<String, DicEntry> getLowTerms() {
        return lowTerms;
    }

    public Map<String, DicEntry> getUpTerms() {
        return upTerms;
    }

    public Map<String, DicEntry> getMixTerms() {
        return mixTerms;
    }

    public Map<String, HashMap<String, PostEntry>> getTmpPosting() {
        return tmpPosting;
    }

    /**
     * merges all the dictionaries into the main dictionary
     * appropriate merges is done in the posting if necessary
     */
    public void setAllTerms(){
        for(String term : mixTerms.keySet()){
            DicEntry entery = mixTerms.get(term);
            DicEntry inUp = upTerms.get(term.toUpperCase());
            DicEntry inLow = lowTerms.get(term.toLowerCase());
            if(inUp != null || inLow != null){
                DicEntry combinedEntery = combineEnteries(term.toLowerCase(), entery, inLow, inUp);
                dic.put(term.toLowerCase(), combinedEntery);
                upTerms.remove(term.toUpperCase());
                lowTerms.remove(term.toLowerCase());
            }
            else {
                dic.put(term.toUpperCase(), entery);
                HashMap<String, PostEntry> postForEntery = tmpPosting.remove(term);
                tmpPosting.put(term.toUpperCase(), postForEntery);

                //forach doc in post docs increase anf check max
                updateDocDetails(postForEntery);
            }
        }
        mixTerms.clear();

        for(Map.Entry<String, DicEntry> entery : upTerms.entrySet()) {
            String term = entery.getKey();
            DicEntry inLow = lowTerms.get(term.toLowerCase());
            if (inLow != null) {
                DicEntry combinedEntery = combineEnteries(term.toLowerCase(), entery.getValue(), inLow, null);
                dic.put(term.toLowerCase(), combinedEntery);
                lowTerms.remove(term.toLowerCase());
            }
            else {
                dic.put(term, entery.getValue());
                //forach doc in post docs increase anf check max
                HashMap<String, PostEntry> postForEntery = tmpPosting.get(term);
                updateDocDetails(postForEntery);
            }
        }
        upTerms.clear();

        for(Map.Entry<String, DicEntry> entery : lowTerms.entrySet()) {
            dic.put(entery.getKey(), entery.getValue());
            //forach doc in post docs increase anf check max
            HashMap<String, PostEntry> postForEntery = tmpPosting.get(entery.getKey());
            updateDocDetails(postForEntery);
        }
        lowTerms.clear();
    }

    /**
     * sets the maxtf and number of unique word in document according to the posting result
     * @param postForEntery - a post of a single term
     */
    private void updateDocDetails(HashMap<String, PostEntry> postForEntery) {
        for (PostEntry pdoc : postForEntery.values()) {
            Doc doc = Main.allDocs.get(pdoc.getDocNumber());
            if (doc!=null) {
                doc.increaseNumOfWords();
                if (doc.getMaxtf() < pdoc.getTf())
                    doc.setMaxtf(pdoc.getTf());
            }
        }
    }

    /**
     *
     * @param term
     * @param entery - the firs dic entry
     * @param inLow - the second dic entry
     * @param inUp - the third dic entry
     * @return a merged dic entry of the three given entries
     */
    private DicEntry combineEnteries(String term, DicEntry entery, DicEntry inLow, DicEntry inUp) {
        DicEntry combinedEntery = new DicEntry(term);
        HashMap<String, PostEntry> combinedPost = new HashMap<>();
        combinePosts(combinedPost, entery.getTerm());

        if(inLow != null) {
            combinedEntery.setTfCourpus(entery.getTfCourpus() + inLow.getTfCourpus());

            combinePosts(combinedPost, inLow.getTerm());
            combinedEntery.setDf(combinedPost.size());
        }
        if(inUp != null) {
            if(inLow == null){
                combinedEntery.setDf(0);
                combinedEntery.setTfCourpus(0);
            }
            combinedEntery.setTfCourpus(combinedEntery.getTfCourpus() + inUp.getTfCourpus());

            combinePosts(combinedPost, inUp.getTerm());
            combinedEntery.setDf(combinedPost.size());
        }
        tmpPosting.put(term, combinedPost);
        //combinedEntery.setPtr();
        return combinedEntery;
    }

    /**
     * combines the two given post entries to one post entry in the tmpposting
     * @param combinedPost - the first post entry
     * @param termToSearch - the second post entry
     */
    private void combinePosts(HashMap<String, PostEntry> combinedPost, String termToSearch) {
        HashMap<String, PostEntry> post = tmpPosting.get(termToSearch);
        for(PostEntry postEntry : post.values()){
            PostEntry p = combinedPost.get(postEntry.getDocNumber());
            //Doc doc = Main.allDocs.stream().filter(x -> x.getDocNumber().equals(postEntry.getDocNumber())).findFirst().get();
            Doc doc = Main.allDocs.get(postEntry.getDocNumber());

            if(p!=null) {
                p.setTf(p.getTf() + postEntry.getTf());
                if(postEntry.isTitle())
                    p.setTitle(true);
                if(doc.getMaxtf()<p.getTf())
                    doc.setMaxtf(p.getTf());
            }
            else {
                combinedPost.put(postEntry.getDocNumber(), postEntry);
                doc.increaseNumOfWords();
                if(doc.getMaxtf()<postEntry.getTf())
                    doc.setMaxtf(postEntry.getTf());
            }
        }
        tmpPosting.remove(termToSearch);
    }

    /**
     * writes data about the docs like maxtf and num of unique words to a file in the dick
     * @param allDocs
     * @param path - the destination path to transfer to
     */
    public void transferDocsData(HashSet<Doc> allDocs, String path) {
        try {
            PrintWriter out=null;
            FileWriter fos=null;
            File file=new File(path + "\\Documents.txt");
            fos=new FileWriter(file,true);
            out=new PrintWriter(fos,true);
            for (String post : Main.allDocs.keySet())
                out.println(Main.allDocs.get(post).toString());
            out.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}


