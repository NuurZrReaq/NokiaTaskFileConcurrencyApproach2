import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class MainClass {


    public static void main (String []args) throws IOException {





        ForkJoinPool pool = new ForkJoinPool();
        FileCount fileCount = new FileCount(Paths.get("directory").toRealPath());
        Long current = System.currentTimeMillis();
        Arrays.stream(pool.invoke(fileCount)).forEach(l->System.out.print(l+" "));
        //fileCount.printLetterCount();
        System.out.println();
        Long after = System.currentTimeMillis();
        System.out.println(after-current);
    }
}

class FileCount extends RecursiveTask<int[]> {



    private Path filePath;
    private  static int [] letterCount = new int[26];


    public FileCount(Path filePath) {
        this.filePath = filePath;
    }




    public  int[] countLowerCase(File file)  {
        int [] tempLetterCount = new int[26];
        try {
            FileInputStream fin = new FileInputStream(file);
            BufferedInputStream fileReader = new BufferedInputStream(fin);
            int c;
            while ((c=fileReader.read() )!= -1){

                if (Character.isLowerCase((char)c)) {

                    try {
                        tempLetterCount[(char)c-'a'] ++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        //System.out.println(fileContent.charAt(i));
                        //System.out.println(letterCount==null);


                    }

                }
            }
            return tempLetterCount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[26];



    }

    @Override
    protected int[] compute() {
        final List <FileCount> walks = new ArrayList<>();
        final List <int[]> countList = new ArrayList<>();
        try{
            Files.walkFileTree(filePath, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (!dir.equals(FileCount.this.filePath)) {
                        FileCount w = new FileCount(dir);
                        w.fork();
                        walks.add(w);

                        return FileVisitResult.SKIP_SUBTREE;
                    } else {
                        return FileVisitResult.CONTINUE;
                    }
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toFile().isFile()) {
                        countList.add(countLowerCase(file.toFile()));
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        for (FileCount w : walks) {
            countList.add(w.join());
        }
        int [][] countArray = new int[countList.size()][26];
        countList.toArray(countArray);
        return incrementLetterAtIndex(countArray);









    }

    public void printLetterCount() {
        Arrays.stream(letterCount).forEach(l->System.out.print(l+" "));
    }

    private   int[] incrementLetterAtIndex(int [] ... tempLetterCounts){
        int [] letterCount = new int[26];
        for(int []temp:tempLetterCounts){
            for(int i=0;i<26;i++){
                letterCount[i]+=temp[i];
            }
        }
        return letterCount;

    }


}
