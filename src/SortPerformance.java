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
        // create array to hold the functions as Consumer Type and their names so loops can be used to verify sorts
        // and run the experiment on it
        String sortNames [] = {"BubbleSort","SelectionSort", "MergeSort", "NaiveQuickSort", "QuickSort"};
        Consumer<long []> [] sorts = (Consumer<long []> [] )new Consumer<?>[5];
        sorts[0] = (long [] list) -> { bubbleSort(list);};
        sorts[1] = (long [] list) -> { selectionSort(list);};
        sorts[2] = (long [] list) -> { mergeSort(list);};
        sorts[3] = (long [] list) -> { naiveQuickSort(list);};
        sorts[4] = (long [] list) -> { quickSort(list);};

        //call checkSortCorrectness on each sort to verify the sort is working correctly
        for(int i = 0; i < 5; i++)
        {
            System.out.println("\n*************************" + sortNames[i] + "*************************");
            checkSortCorrectness(sorts[i]);
        }

        String filename;
        // run the experiment 4 times for each sort, adjust the MAXINPUTSIZE to the maximum inputsize the sort can handle in
        // a reasonable amount of time
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

        // run the experiment 4 times for Naive Quick Sort and Quick Sort with Sorted lists
        // the final argument in runFullExperiment, true, signals that the lists should be sorted before calling the sort function
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

    // verify that the elements of the array passed in are already sorted
    static boolean verifySorted(long [] list)
    {
        for(int i = 0; i < list.length - 1; i ++)
            if (list[i] > list[i+1])
                return false;
        return true;
    }

    // verify that the passed in sort function sorts the array correctly
    // visually verify by printing out a small array before and after sorting
    // verify using verifySorted verify on larger lists
    static void checkSortCorrectness(Consumer<long []> sort)
    {
        //visually check sort algorithm on 3 small lists
        System.out.println("\nVerifying visually on lists of 10: ");
        for ( int i = 0; i < 3; i++)
        {
            long [] testList = createRandomIntegerList(10);
            System.out.println("List " + (i + 1) + " BEFORE sort: " + Arrays.toString(testList));
            sort.accept(testList);
            System.out.println("List " + (i + 1) + " AFTER  sort: " + Arrays.toString(testList));
        }

        //verify on 3 larger lists of 5000 by calling verifySorted
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


    // sort an array using bubble sort algorithm
    public static void bubbleSort(long[] list)
    {
        // make N passes through the list
        for(int i = 0; i< list.length-1; i++)
        {
            // for index from 0 to n-1, compare item[index] to next, swap if needed
            for (int j = 0; j < list.length - 1 - i; j++)
            {
                if (list[j] > list[j + 1])
                {
                    long temp = list[j];
                    list[j] = list[j + 1];
                    list[j + 1] = temp;
                }
            }
        }
    }

    // sort an array using selection sort algorithm
    public static void selectionSort (long [] list){
        int minIndex;
        long min;

        //make N-1 passes through the list
        for (int i = 0; i < list.length-1; i++)
        {
            minIndex = i;
            min = list[minIndex];

            // for index i to N
            // if element at index i is less than min, set element at index as min
            for(int j= i; j < list.length; j++)
                if (list[j] < min) {
                    minIndex = j;
                    min = list[minIndex];
                }

            // swap current element with the min element found above
            list[minIndex] = list[i];
            list[i] = min;
        }
    }

    // sort an array using merge sort algorithm
    static void  mergeSort (long[] list)
    {
        int len = list.length;

        // once array is only 1 element, return
        if (len <= 1)
           return;

        // calculate middle of arrays and
        // create one array to hold the first half of the array and one to hold the second half
        int mid = len/2;
        long L [] = new long [mid];
        long R [] = new long [len - mid];

        // copy first half of array into first array
        for(int i = 0; i < L.length; i++)
        {
            L[i] = list[i];
        }

        // copy second half of the array into second array
        for(int i = 0; i < R.length; i++)
        {
            R[i] = list[i + mid];
        }

        // call merge sort on both halves of the array
        // then merge both halves of the array together
        mergeSort(L);
        mergeSort(R);
        merge (list, L, R);

    }

    // merge function to merge two arrays together in the sorted order
    static void  merge (long [] list, long[] A, long[] B)
    {

        int iA = 0, iB = 0, iM = 0;

        //while index of A and index of B are both still in range
        while (iA < A.length && iB < B.length)
        {
            // if current element in A is less than or equal to current element in B
            // copy current element in A into merged list else copy current element in B into merged list
            if(A[iA] <= B[iB])
            {
                list[iM++] = A[iA++];
            }
            else
            {
                list[iM++] = B[iB++];
            }
        }

        // while index of B is out of range and A is still in range, copy the rest of A into merged list
        while (iA < A.length)
        {
            list[iM++] = A[iA++];
        }
        // while index of A is out of range and B is still in range, copy the rest of B into merged list
        while (iB < B.length)
        {
            list[iM++] = B[iB++];
        }

        return;
    }

    //naiveQuickSort wrapper function
    static void naiveQuickSort(long [] list)
    {
        naiveQuickSort(list, 0, list.length-1);
    }

    // sort an array using naive quicksort algorithm
    static void naiveQuickSort(long [] list, int low, int high)
    {
        int pivotIndex, nextHi;
        long pivot, temp;

        // when list is 0 or 1 element return
        if (high < 0 || low >= high)
            return;
        else
        {
            // set first number to be the pivot
            pivotIndex = low;
            pivot = list[pivotIndex];
            nextHi = high;

            // while pivotIndex and nextHi haven't met up
            while (pivotIndex < nextHi)
            {
                // if the element to the right of the pivot is less than the pivot
                // swap the element with pivot otherwise swap the element with nextHi
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

            //call quick sort on the first half and the last half of the array
            naiveQuickSort(list, low, pivotIndex-1);
            naiveQuickSort(list, nextHi+1, high);


        }
    }

    // quickSort wrapper function
    static void quickSort(long [] list)
    {
        quickSort(list, 0, list.length-1);
    }

    // sort an array using quick sort algorithm
    static void quickSort(long [] list, int low, int high)
    {
        int pivotIndex, nextHi;
        long pivot, temp;

        // when list is 0 or 1 element return
        if (high < 0 || low >= high)
            return;
        else
        {
            // generate a random number to be the pivotIndex
            pivotIndex = (int)(low + Math.random() * (high-low));
            pivot = list[pivotIndex];
            list[pivotIndex] = list[low];
            list[low] = pivot;
            pivotIndex = low;
            nextHi = high;

            // while pivotIndex and nextHi haven't met up
            while (pivotIndex < nextHi)
            {
                // if the element to the right of the pivot is less than the pivot
                // swap the element with pivot otherwise swap the element with nextHi
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

            //call quick sort on the first half and the last half of the array
            quickSort(list, low, pivotIndex-1);
            quickSort(list, nextHi+1, high);


        }
    }

    // runs the sort function specified in function call for every input size for the specified number of trials
    // times the amount of time each trial took, and calculates the average for the input size
    // prints the input size along with the average time taken to run the sort function
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
                if (sorted)                                                                             // if sorted flag is true, sort the array before
                    Arrays.sort(testList);

                TrialStopwatch.start();                                                                 // begin timing
                sort.accept(testList);                                                                  // run the sort function on the trial input
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime();                     // stop timer and add to the total time elapsed for the batch of trials
            }
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials;     // calculate the average time per trial in this batch 

            resultsWriter.printf("%12d  %15.2f \n",inputSize, averageTimePerTrialInBatch);              // print data for this size of input
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }
}
