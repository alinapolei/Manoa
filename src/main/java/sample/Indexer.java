package sample;

import java.io.*;
import java.util.*;


public class Indexer {

    private Map<String, DicEntry> dic;
    private Map<String, DicEntry> lowTerms;
    private Map<String, DicEntry> upTerms;
    private Map<String, DicEntry> mixTerms;
    private Map<String, HashMap<String, PostEntry>> tmpPosting;
    Conditions con;



    public Indexer() {
        dic = Collections.synchronizedMap(new HashMap<>());
        lowTerms = Collections.synchronizedMap(new HashMap<>());
        upTerms = Collections.synchronizedMap(new HashMap<>());
        mixTerms = Collections.synchronizedMap(new HashMap<>());
        tmpPosting = Collections.synchronizedMap(new HashMap<>());
        con = new Conditions();
    }

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

    public void setLowerTerms(String term,Doc doc, boolean isTitle) {
        if (!this.lowTerms.containsKey(term))
            newTerm(term, doc, this.lowTerms, isTitle);
        else{
            int x=this.lowTerms.get(term).getTfCourpus()+1;
            this.lowTerms.get(term).setTfCourpus(x) ;
            editTerm(term, doc, this.lowTerms, isTitle);
        }
    }

    public void setUpperTerms(String term,Doc doc, boolean isTitle) {
        if (!this.upTerms.containsKey(term))
            newTerm(term, doc, this.upTerms, isTitle);
        else{
            this.upTerms.get(term).setTfCourpus( this.upTerms.get(term).getTfCourpus()+1);
            editTerm(term, doc, this.upTerms, isTitle);
        }
    }

    public void setMixedTerms(String term,Doc doc, boolean isTitle) {
        if (!this.mixTerms.containsKey(term))
            newTerm(term, doc, this.mixTerms, isTitle);
        else{
            this.mixTerms.get(term).setTfCourpus( this.mixTerms.get(term).getTfCourpus()+1);
            editTerm(term, doc, this.mixTerms, isTitle);
        }
    }
    private void newTerm(String term, Doc doc, Map<String, DicEntry> terms, boolean isTitle) {
        DicEntry dicEntry=new DicEntry(term);
        //PostEntry post =new PostEntry(doc.getDocNumber(), isTitle);
        terms.put(term, dicEntry);
        HashMap<String, PostEntry> linkedList = new LinkedHashMap<>();
        linkedList.put(doc.getDocNumber(), new PostEntry(doc.getDocNumber(), isTitle));
        tmpPosting.put(term, linkedList);
    }
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
    public void removeStopWordfromtmpPosting( Set<String> stopWords) {
        for (int i = 0; i < stopWords.size(); i++) {
            // if (Main.indexer.getTmpPosting().containsKey(stopWords.toArray()[i].toString().toLowerCase()))
            Main.indexer.getTmpPosting().remove(stopWords.toArray()[i].toString().toLowerCase());
            // if (Main.indexer.getTmpPosting().containsKey(stopWords.toArray()[i].toString().toUpperCase()))
            Main.indexer.getTmpPosting().remove(stopWords.toArray()[i].toString().toUpperCase());
            String tmp = Character.toUpperCase((stopWords.toArray()[i]).toString().toCharArray()[0]) + (stopWords.toArray()[i]).toString().substring(1).toLowerCase();
            //if (Main.indexer.getTmpPosting().containsKey(tmp)) ;
            Main.indexer.getTmpPosting().remove(tmp);

        }
    }
    public void removeStopWords(Set<String> stopWords) {
        for (int i = 0; i < stopWords.size(); i++) {
            Main.indexer.getDic().remove(stopWords.toArray()[i]);
            String tmp = Character.toUpperCase((stopWords.toArray()[i]).toString().toCharArray()[0]) + (stopWords.toArray()[i]).toString().substring(1).toLowerCase();
            Main.indexer.getMixTerms().remove(tmp);
            Main.indexer.getUpTerms().remove(stopWords.toArray()[i].toString().toUpperCase());
            Main.indexer.getLowTerms().remove(stopWords.toArray()[i].toString().toLowerCase());
        }
    }


    public void transferToDisk(String path) throws IOException {
        removeStopWordfromtmpPosting(Main.stopWords);
        removeStopWords(Main.stopWords);
        setAllTerms();
        System.out.println("[+]Finish setall Terms");
        HashMap<String, PostEntry> list;
        File file;
        ArrayList<String> sortList=a();
        Map<Character, ArrayList<String>>Finalsort=b(sortList);
        PrintWriter out=null;
        FileWriter fos=null;
        for(Character x : Finalsort.keySet()) {
            Character character=x;
            if(character=='"'||character==' '||character=='?')
                file=new File(path+"\\"+"rest"+".txt");
            else
                file = new File(path + "\\" + x + ".txt");


            /*
            else if (term.toCharArray()[0]=='$')
                file = new File(path + "\\" + "$" + ".txt");
            else if (term.toCharArray()[0]=='0')
                file = new File(path + "\\" + "0" + ".txt");
            else if (term.toCharArray()[0]=='1')
                file = new File(path + "\\" + "1" + ".txt");
            else if (term.toCharArray()[0]=='2')
                file = new File(path + "\\" + "2" + ".txt");
            else if (term.toCharArray()[0]=='3')
                file = new File(path + "\\" + "3" + ".txt");
            else if (term.toCharArray()[0]=='4')
                file = new File(path + "\\" + "4" + ".txt");
            else if (term.toCharArray()[0]=='5')
                file = new File(path + "\\" + "5" + ".txt");
            else if (term.toCharArray()[0]=='6')
                file = new File(path + "\\" + "6" + ".txt");
            else if (term.toCharArray()[0]=='7')
                file = new File(path + "\\" + "7" + ".txt");
            else if (term.toCharArray()[0]=='8')
                file = new File(path + "\\" + "8" + ".txt");
            else if (term.toCharArray()[0]=='9')
                file = new File(path + "\\" + "9" + ".txt");
            else
                file = new File(path + "\\" + "tmp" + ".txt");
            */
            sortList.clear();
            sortList = Finalsort.get(x);
            for (String term : sortList) {
                list = tmpPosting.get(term);
                 fos = new FileWriter(file, true);
                out = new PrintWriter(fos, true);
                String t = "";
                for (PostEntry post : list.values())
                    t = t + post.toString() + ", ";
                out.println(term + " " + t);

            }
            if (out!=null)
                fos.flush();
                out.close();
                out = null;
                System.gc();
            if (fos!=null) {
                fos.close();
                fos=null;
            }
            // if(out!=null)
            //    out.close();
        }
            //out.close();
        tmpPosting.clear();
    }
    public ArrayList<String> a() {
        ArrayList<String> sortedKeys =
                new ArrayList<String>(tmpPosting.keySet());
        Collections.sort(sortedKeys);
        return sortedKeys;
    }
    public Map<Character, ArrayList<String>> b(ArrayList<String>sortList){
        Map <Character,ArrayList<String>> MapAlpha=new HashMap<>();
        for(String x :sortList){
            Character tmp= Character.toLowerCase(x.toCharArray()[0]);
            if(!MapAlpha.containsKey(tmp)) {
                MapAlpha.put(tmp, new ArrayList<>());
                MapAlpha.get(tmp).add(x);
            }
            else
                MapAlpha.get(tmp).add(x);
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
    public void setAllTerms(){
        for(String term : mixTerms.keySet()){
            //String term = entery.getKey();
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
    public void transferDocsData(HashSet<Doc> allDocs, String path) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path + "\\Documents.txt"));
            Iterator it = allDocs.iterator();
            while(it.hasNext()) {
                out.write(it.next().toString());
                out.newLine();
            }
            out.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}


