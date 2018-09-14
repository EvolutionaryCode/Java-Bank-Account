package org.aagrandpre.apsci;
//Import Java Packages
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Austin Grandpre
 * August 29th, 2018
 * Period - 1
 * Array - Printing out mins,max's,sum and average to a user imputed set of numbers
 * 
 * Changelog - 
 * August 30th, 2019 - Added Decimal Format & Connected to userInput 
 * 
 */

public class comsci {
public static void main(String[] args)
    {
 
        // Initializing array of integers
        Double[] num = UserInput.grabInput(); //Connects to UserImput to grab 
        
        // using Collection.min() to find minimum element
        Double min = Collections.min(Arrays.asList(num));
 
        // using Collection.max() to find maximum element
        Double max = Collections.max(Arrays.asList(num));
        
        
        //Finds the sum of the numbers in the array
        double sum = 0;
        for (double i : num)
            sum += i;
              
 
        // printing min, max, sum, average
        System.out.println("Minimum number of array is : " + DF.format(min));
        System.out.println("Maximum number of array is : " + DF.format(max));
        System.out.println("The Sum of the array is : " + DF.format(sum));
        System.out.println("The Average of the array is : " +  DF.format(sum/num.length));
        
        
    }
}

		