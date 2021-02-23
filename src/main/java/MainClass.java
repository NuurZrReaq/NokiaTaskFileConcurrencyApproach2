import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class MainClass {


    public static void main (String []args){



        File dir = new File ("directory");


        ForkJoinPool pool = new ForkJoinPool();
        FileCount fileCount = new FileCount(dir);
        Long current = System.currentTimeMillis();
        pool.invoke(fileCount);
        fileCount.printLetterCount();
        System.out.println();
        Long after = System.currentTimeMillis();
        System.out.println(after-current);
    }
}

class FileCount extends RecursiveAction {
    private File file;
    private  static int [] letterCount = new int[26];

    public FileCount(File file) {
        this.file = file;
    }




    public  void countLowerCase(File file)  {

        try {
            FileInputStream fin = new FileInputStream(file);
            BufferedInputStream fileReader = new BufferedInputStream(fin);
            int c;
            while ((c=fileReader.read() )!= -1){

                if (Character.isLowerCase((char)c)) {

                    try {
                        incrementLetterAtIndex((char)c-'a');
                    } catch (Exception e) {
                        e.printStackTrace();
                        //System.out.println(fileContent.charAt(i));
                        //System.out.println(letterCount==null);


                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    protected void compute() {

        if(file.isFile()){
            countLowerCase(file);
        }
        else {

            File [] subFiles = file.listFiles();

            if(subFiles!=null&&subFiles.length >0){
                FileCount [] fileCounts = new FileCount[subFiles.length];
                for(int i=0;i< fileCounts.length-1;i++){
                    fileCounts[i] = new FileCount(subFiles[i]);
                    fileCounts[i].fork();
                }




                fileCounts[fileCounts.length-1] = new FileCount(subFiles[fileCounts.length-1]);
                fileCounts[fileCounts.length-1].compute();
                for(int i=0;i< fileCounts.length-1;i++){
                     fileCounts[i].join();
                }


            }


        }




    }

    public void printLetterCount() {
        Arrays.stream(letterCount).forEach(l->System.out.print(l+" "));
    }

    private static synchronized void incrementLetterAtIndex(int index){
        letterCount[index]++;
    }


}
