import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class MainClass {


    public static void main (String []args){
       /* System.out.println(Runtime.getRuntime().totalMemory()/1000000 + "  ..........    "+Runtime.getRuntime().freeMemory()+"......."+ (double)(Math.pow(2,10)));
        System.out.printf("%.3fGiB\n", Runtime.getRuntime().maxMemory() / Math.pow(2,30) );
        System.out.println(Runtime.getRuntime().availableProcessors());*/


        File dir = new File ("directory");


        /*File [] files = dir.listFiles();
        Arrays.stream(files).forEach(f->System.out.println(f.getName()));*/
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




    public  void countLowerCase(File file) {


        StringBuilder fileContent = new StringBuilder();
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(file.getName());
        }
        while(fileReader.hasNextLine()){
            fileContent.append(fileReader.nextLine());
        }
        for (int i = 0; i < fileContent.length(); i++) {

            if (Character.isLowerCase(fileContent.charAt(i))) {


                incrementLetterAtIndex(fileContent.charAt(i) - 'a');
                //System.out.println("shit");

            }
        }
        fileReader.close();


        //return letterCount;
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

                //Integer [] joinedLetterCount = joinAllArrays(fileCounts[fileCounts.length-1].compute(), Arrays.stream(fileCounts).peek(l->l.join()).toArray(Integer[]::new));

//                System.out.println(Runtime.getRuntime().availableProcessors());
//                System.out.println(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
            }


        }




    }

    public void printLetterCount() {
        Arrays.stream(letterCount).forEach(l->System.out.print(l+" "));
    }

    private static synchronized void incrementLetterAtIndex(int index){
        letterCount[index]++;
    }


    /*@Override
    protected int[] compute(){
        int[] letterCount = new int[26];
        int [] tempLetterCount ;
        File [] files = Arrays.stream(file.listFiles()).filter(f->f.isFile()).toArray(File[]::new);
        //System.out.println("files   "+files.length);
        File [] dirs = Arrays.stream(file.listFiles()).filter(f->f.isDirectory()).toArray(File[]::new);
        //System.out.println(dirs.length);
        if(files != null&&files.length>0) {
            for(File f:files){
                tempLetterCount = countLowerCase(f);
                letterCount = joinAllArrays(letterCount,tempLetterCount);
            }
        }

        if(dirs!=null && dirs.length>0){
            FileCount[] fileCounts = new FileCount[dirs.length];
            for(int i=0;i< dirs.length-1;i++){
                fileCounts[i] = new FileCount(dirs[i]);
                fileCounts[i].fork();
            }

            fileCounts[dirs.length-1] = new FileCount(dirs[dirs.length-1]);
            int [][] joinedLetterCount = new int[fileCounts.length][26];

            joinedLetterCount[fileCounts.length -1] = joinAllArrays(fileCounts[fileCounts.length-1].compute(),letterCount);
            for(int i=0;i< fileCounts.length-1;i++){
                joinedLetterCount[i] = fileCounts[i].join();
            }
            return joinAllArrays(joinedLetterCount);
        }
        return letterCount;

    }*/

   /* private int[] joinAllArrays(int[] ... args){
        int [] letterCounts = new int[26];

        Arrays.stream(args).forEach(l-> {
            for(int i=0;i<26;i++){
                letterCounts[i] += l[i];
            }
        });
        return letterCounts;
    }*/
}
