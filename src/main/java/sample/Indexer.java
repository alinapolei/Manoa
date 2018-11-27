package sample;

import javafx.geometry.Pos;

import java.io.*;
import java.nio.file.Files;
import java.util.*;


public class Indexer {

    private Map<String,DicEntry> dic;
    private Map <String,DicEntry> lowTerms;
    private Map <String,DicEntry> upTerms;
    private Map <String,DicEntry> mixTerms;
    private Map <String, HashMap <String, Integer>> tmpPosting;
    Conditions con=new Conditions();
    public Indexer() {
        dic=Collections.synchronizedMap(new HashMap<>());
        lowTerms=Collections.synchronizedMap(new HashMap<>());
        upTerms=Collections.synchronizedMap(new HashMap<>());
        mixTerms=Collections.synchronizedMap(new HashMap<>());
        tmpPosting=Collections.synchronizedMap(new HashMap<>());
    }

    public void setDic(String term,Doc doc) {
        if (!this.dic.containsKey(term))
            newTerm(term, doc, this.dic);
        else{
            this.dic.get(term).setTfCourpus( this.dic.get(term).getTfCourpus()+1);
            editTerm(term, doc);
        }
    }

    public void setLowerTerms(String term,Doc doc) {
        if (!this.lowTerms.containsKey(term))
            newTerm(term, doc, this.lowTerms);
        else{
            int x=this.lowTerms.get(term).getTfCourpus()+1;
            this.lowTerms.get(term).setTfCourpus(x) ;
            editTerm(term, doc);
        }
    }

    public void setUpperTerms(String term,Doc doc) {
        if (!this.upTerms.containsKey(term))
            newTerm(term, doc, this.upTerms);
        else{
            this.upTerms.get(term).setTfCourpus( this.upTerms.get(term).getTfCourpus()+1);
            editTerm(term, doc);
        }
    }

    public void setMixedTerms(String term,Doc doc) {
        if (!this.mixTerms.containsKey(term))
            newTerm(term, doc, this.mixTerms);
        else{
            this.mixTerms.get(term).setTfCourpus( this.mixTerms.get(term).getTfCourpus()+1);
            editTerm(term, doc);
        }
    }

    private void newTerm(String term, Doc doc, Map<String, DicEntry> terms) {
        DicEntry dicEntry=new DicEntry(term);
        PostEntry post =new PostEntry(doc.getDocNumber());
        terms.put(term, dicEntry);
        HashMap<String, Integer> linkedList = new LinkedHashMap<>();
        linkedList.put(doc.getDocNumber(), 1);
        tmpPosting.put(term, linkedList);
        //tmpPosting.get(term).add((post));
    }
    private void editTerm(String term, Doc doc) {
        /*int res;
        res = conInPosting(doc.getDocNumber(), term);
        if(res==-1)
            tmpPosting.get(term).add(new PostEntry(doc.getDocNumber()));
        else {
            ((PostEntry)tmpPosting.get(term).toArray()[res]).increaseTf();
           }*/
        HashMap<String, Integer> postEntries = tmpPosting.get(term);
        Integer post = postEntries.get(doc.getDocNumber());//conInPosting(doc.getDocNumber(), term);
        if(post!=null)
            post++;
        else
            postEntries.put(doc.getDocNumber(), 1);
    }

    public Map<String, DicEntry> getDic() {
        return dic;
    }

    private PostEntry conInPosting(String docNumber, String term) {
        /*//int i=0;
        for (PostEntry post : tmpPosting.get(term)) {
            if (post.getDocNumber().equals(docNumber))
                return post;
                //return i;
            //i++;
        }*/
        return null;
    }


    /*public synchronized void transferToDisk() throws IOException {
        LinkedList<PostEntry> list;
        File file;
        for (String term : tmpPosting.keySet())
        {
            list=tmpPosting.get(term);
            if(con.isAlpha(term))
                file =new File ("C:\\Users\\alina\\Desktop\\Posting\\"+term.substring(0,1).toUpperCase()+".txt");
            else
                file =new File ("C:\\Users\\alina\\Desktop\\Posting\\"+"Numbers"+".txt");
                FileWriter outFile = new FileWriter(file,true);
            for(PostEntry post: list)
                try {
                    PrintWriter out = new PrintWriter(outFile);
                    try {
                        out.append(post.toString());
                            file.length();
                    } finally {
                        out.close();
                    }
                } finally {
                    outFile.close();
                }
        }

        }*/
}
