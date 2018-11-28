package sample;

import java.io.*;
import java.nio.file.Files;
import java.util.*;


public class Indexer {

    private Map<String,DicEntry> dic;
    private Map <String,DicEntry> lowTerms;
    private Map <String,DicEntry> upTerms;
    private Map <String,DicEntry> mixTerms;
    private Map <String, LinkedList <PostEntry>> tmpPosting;
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
        {
            newTerm(term, doc, this.dic);
        }
        else{
            this.dic.get(term).setTfCourpus( this.dic.get(term).getTfCourpus()+1);
            editTerm(term, doc);
        }
    }

    public void setLowerTerms(String term,Doc doc) {
        if (!this.lowTerms.containsKey(term))
        {
            newTerm(term, doc, this.lowTerms);
        }
        else{
            int x=this.lowTerms.get(term).getTfCourpus()+1;
            this.lowTerms.get(term).setTfCourpus(x) ;
            editTerm(term, doc);
        }
    }

    public void setUpperTerms(String term,Doc doc) {
        if (!this.upTerms.containsKey(term))
        {
            newTerm(term, doc, this.upTerms);
        }
        else{
            this.upTerms.get(term).setTfCourpus( this.upTerms.get(term).getTfCourpus()+1);
            editTerm(term, doc);
        }
    }

    public void setMixedTerms(String term,Doc doc) {
        if (!this.mixTerms.containsKey(term))
        {
            newTerm(term, doc, this.mixTerms);
        }
        else{
            this.mixTerms.get(term).setTfCourpus( this.mixTerms.get(term).getTfCourpus()+1);
            editTerm(term, doc);
        }
    }

    private void newTerm(String term, Doc doc, Map<String, DicEntry> lowTerms) {
        DicEntry dicEntry=new DicEntry(term);
        PostEntry post =new PostEntry(doc.getDocNumber());
        lowTerms.put(term, dicEntry);
        tmpPosting.put(term,new LinkedList<>());
        tmpPosting.get(term).add((post));
    }
    private synchronized void editTerm(String term, Doc doc) {
        int res = conInPosting(doc.getDocNumber(), term);
        if(res==-1)
            tmpPosting.get(term).add(new PostEntry(doc.getDocNumber()));
        else {
            tmpPosting.get(term).get(res);
               tmpPosting.get(term).get(res).increaseTf();
           }
    }

    public Map<String, DicEntry> getDic() {
        return dic;
    }

    private int conInPosting(String docNumber, String term) {
        int i=0;
        synchronized (tmpPosting) {
            for (PostEntry post : tmpPosting.get(term)) {
                if (post.getDocNumber().equals(docNumber))
                    return i;
                i++;
            }
        }
    return -1;
}


    public synchronized void transferToDisk() throws IOException {
        LinkedList<PostEntry> list;
        File file;
        for (String term : tmpPosting.keySet())
        {
            list=tmpPosting.get(term);
            if(con.isAlpha(term))
                file =new File ("C:\\Users\\Dror\\Desktop\\Posting\\"+term.substring(0,1).toUpperCase()+".txt");
            else
                file =new File ("C:\\Users\\Dror\\Desktop\\Posting\\"+"Numbers"+".txt");
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

        }
}
