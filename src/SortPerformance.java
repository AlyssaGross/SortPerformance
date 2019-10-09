import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.io.*;
import java.util.Arrays;
import java.util.function.Consumer;

public class SortPerformance {
    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE = 2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 50;
    static int MAXINPUTSIZE = (int) Math.pow(2,23);
    static int MININPUTSIZE = 1;

    //set up variable to hold folder path and FileWriter/PrintWriter for printing results to a file
    static String ResultsFolderPath = "/home/alyssa/Results/"; // pathname to results folder 
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;


    public static void main (String[] args)
    {
        String sortNames [] = {"BubbleSort","SelectionSort", "MergeSort", "NaiveQuickSort", "QuickSort"};
        Consumer<long []> [] sorts = (Consumer<long []> [] )new Consumer<?>[5];
        sorts[0] = (long [] list) -> { bubbleSort(list);};
        sorts[1] = (long [] list) -> { selectionSort(list);};
        sorts[2] = (long [] list) -> { mergeSort(list);};
        sorts[3] = (long [] list) -> { naiveQuickSort(list);};
        sorts[4] = (long [] list) -> { quickSort(list);};

        for(int i = 0; i < 5; i++)
        {
            System.out.println("\n*************************" + sortNames[i] + "*************************");
            checkSortCorrectness(sorts[i]);
        }

        String filename;
        for(int i = 0; i<5; i++)
        {
            if ( i == 0 )
                MAXINPUTSIZE = (int) Math.pow(2,16);
            else if (i == 1)
                MAXINPUTSIZE = (int) Math.pow(2,17);
            else
                MAXINPUTSIZE = (int) Math.pow(2,24);

            for (int k = 0; k<4; k++) {
                System.out.println("\nExperiment Run " + (k+1) + " of " + sortNames[i]);
                System.out.println("------------------------------------------------------");
                filename = sortNames[i] + "-Exp" + (k+1) + ".txt";
                runFullExperiment(sorts[i],filename,  false);
            }
        }

        MAXINPUTSIZE = (int) Math.pow(2,20);
        for(int i = 3; i <5; i ++)
        {
            for (int k = 0; k<4; k++) {
                System.out.println("\nExperiment Sorted Run " + (k + 1) + " of " + sortNames[i]);
                System.out.println("------------------------------------------------------");
                filename = sortNames[i] + "-SortedExp" + (k + 1) + ".txt";
                runFullExperiment(sorts[i], filename, true);
            }
        }




     }

    //create a random list of integers of a specific length
    static long[] createRandomIntegerList(int size)
    {
        long [] newList = new long[size];
        for(int j=0; j<size; j++)
        {
            newList[j] = (long)(MINVALUE + Math.random() * (MAXVALUE - MINVALUE));
        }
        return newList;
    }

    //verifies that the elements of the array passed in are already sorted
    static boolean verifySorted(long [] list)
    {
        for(int i = 0; i < list.length - 1; i ++)
            if (list[i] > list[i+1])
                return false;
        return true;
    }

    static void checkSortCorrectness(Consumer<long []> sort) {
        //visually check sort algorithm on 3 small lists
        System.out.println("\nVerifying visually on lists of 10: ");
        for ( int i = 0; i < 3; i++)
        {
            long [] testList = createRandomIntegerList(10);
            System.out.println("List " + (i + 1) + " BEFORE sort: " + Arrays.toString(testList));
            sort.accept(testList);
            System.out.println("List " + (i + 1) + " AFTER  sort: " + Arrays.toString(testList));
        }

        //verify on 3 large lists
        System.out.println("\nVerifying on lists of 5000: ");
        for (int i = 0; i < 3; i++)
        {
            long [] testList = createRandomIntegerList(5000);
            sort.accept(testList);
            if ( verifySorted(testList))
                System.out.println("List " +(i + 1) + ": Sorted");
            else
                System.out.println("List " +(i + 1) + ": NOT Sorted");
        }



    }

    public static void bubbleSort(long[] list)
    {
        /* make N passes through the list*/
        for(int i = 0; i< list.length-1; i++)
        {
            /* for index from 0 to n-1, compare item[index] to next, swap if needed */
            for (int j = 0; j < list.length - 1 - i; j++)
            {
                if (list[j] > list[j + 1]) {
                    long temp = list[j];
                    list[j] = list[j + 1];
                    list[j + 1] = temp;
                }
            }
        }

    }

    public static void selectionSort (long [] list){
        int minIndex;
        long min;
        for (int i = 0; i < list.length-1; i++){
            minIndex = i;
            min = list[minIndex];
            for(int j= i; j < list.length; j++) {
                if (list[j] < min) {
                    minIndex = j;
                    min = list[minIndex];
                }
            }
            list[minIndex] = list[i];
            list[i] = min;
        }
    }

    static void  mergeSort (long[] list)
    {
        int len = list.length;
        if (len <= 1)
           return; // return list;

        int mid = len/2;

        long L [] = new long [mid];
        long R [] = new long [len - mid];

        for(int i = 0; i < L.length; i++)
        {
            L[i] = list[i];
        }

        for(int i = 0; i < R.length; i++)
        {
            R[i] = list[i + mid];
        }

        mergeSort(L);
        mergeSort(R);
        merge (list, L, R);

    }

    static void  merge (long [] list, long[] A, long[] B)
    {

        int iA = 0, iB = 0, iM = 0;

        while (iA < A.length && iB < B.length)
        {
            if(A[iA] <= B[iB])
            {
                list[iM++] = A[iA++];
            }
            else
            {
                list[iM++] = B[iB++];
            }
        }
        while (iA < A.length)
        {
            list[iM++] = A[iA++];
        }
        while (iB < B.length)
        {
            list[iM++] = B[iB++];
        }

        return;
    }

    static void naiveQuickSort(long [] list)
    {
        naiveQuickSort(list, 0, list.length-1);
    }

    static void naiveQuickSort(long [] list, int low, int high)
    {
        int pivotIndex, nextHi;
        long pivot, temp;
        if (high < 0 || low >= high)
            return;
        else
        {
            pivotIndex = low;
            pivot = list[pivotIndex];
            nextHi = high;
            while (pivotIndex < nextHi)
            {
                if ( list[pivotIndex+1] < pivot)
                {
                    list[pivotIndex]   = list[pivotIndex+1];
                    list[pivotIndex+1] = pivot;
                    pivotIndex++;
                }
                else
                {
                    temp = list[nextHi];
                    list[nextHi] = list[pivotIndex+1];
                    list[pivotIndex+1] = temp;
                    nextHi--;
                }
            }

            naiveQuickSort(list, low, pivotIndex-1);
            naiveQuickSort(list, nextHi+1, high);


        }
    }

    static void quickSort(long [] list)
    {
        quickSort(list, 0, list.length-1);
    }

    static void quickSort(long [] list, int low, int high)
    {
        int pivotIndex, nextHi;
        long pivot, temp;
        if (high < 0 || low >= high)
            return;
        else
        {
            pivotIndex = (int)(low + Math.random() * (high-low));
            pivot = list[pivotIndex];
            list[pivotIndex] = list[low];
            list[low] = pivot;
            pivotIndex = low;
            nextHi = high;
            while (pivotIndex < nextHi)
            {
                if ( list[pivotIndex+1] < pivot)
                {
                    list[pivotIndex]   = list[pivotIndex+1];
                    list[pivotIndex+1] = pivot;
                    pivotIndex++;
                }
                else
                {
                    temp = list[nextHi];
                    list[nextHi] = list[pivotIndex+1];
                    list[pivotIndex+1] = temp;
                    nextHi--;
                }
            }

            quickSort(list, low, pivotIndex-1);
            quickSort(list, nextHi+1, high);


        }
    }


    static void runFullExperiment(Consumer<long []> sort, String resultsFileName, boolean sorted){

        int count;
        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return;
        }


        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch();                                   // create stopwatch for timing an individual trial 

        resultsWriter.println("#InputSize    AverageTime");                                             // # marks a comment in gnuplot data 
        resultsWriter.flush();

        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*= 2) {                        // for each size of input we want to test: starting at MININPUTSIZE and doubling each iteration until reaching MAXINPUTSIZE

            System.out.println("Running test for input size "+inputSize+" ... ");                       // progress message... 
            System.out.print("    Running trial batch...");
            long batchElapsedTime = 0;                                                                  // reset elapsed time for the batch to 0

            System.gc();                                                                                // force garbage collection before each batch of trials run

            // repeat for desired number of trials (for a specific size of input)...
            for (long trial = 0; trial < numberOfTrials; trial++) {                                     // run the trials 

                long[] testList = createRandomIntegerList(inputSize);
                if (sorted)
                    Arrays.sort(testList);

                TrialStopwatch.start();                                                                 // begin timing
                sort.accept(testList);                                                                  // run the threeSumFaster on the trial input
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime();                     // stop timer and add to the total time elapsed for the batch of trials
            }
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials;     // calculate the average time per trial in this batch 

            resultsWriter.printf("%12d  %15.2f \n",inputSize, averageTimePerTrialInBatch);              // print data for this size of input
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }
}
