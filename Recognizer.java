import java.awt.Point;
import java.io.*;

/**
 * This class performs unistroke handwriting recognition using an algorithm
 * known as "elastic matching."
 * 
 * @author Dave Berque
 * @version August, 2004 Slightly modified by David E. Maharry and Carl Singer
 *          10/27/2004
 * 
 */

public class Recognizer {
    public static final int STROKESIZE = 150; // Max number of points in each
                                                // stroke
    private static final int NUMSTROKES = 10; // Number of strokes in the base
                                                // set (0 through 9)
    private Point[] userStroke; // holds points that comprise the user's stroke
    private int nextFree; // next free cell of the userStroke array

    private Point[][] baseSet; // holds points for each stroke (0-9) in the
                                // base set.

    // baseset is an array of arrays, a 2-D array.

    /**
     * Constructor for the recognizer class. Sets up the arrays and loads the
     * base set from an existing data file which is assumed to have the right
     * number of points in it. The file is organized so that there are 150
     * points for stroke 0 followed by 150 points for stroke 1, ... 150 poinpts
     * for stroke 9. Each stroke is organized as an alternating series of x, y
     * pairs. For example, stroke 0 consists of 300 lines with the first line
     * being x0 for stroke 0, the next line being y0 for stroke 0, the next line
     * being x1 for stroke 0 and so on.
     */
    public Recognizer()
    {
        int row, col, stroke, pointNum, x, y;
        String inputLine;

        userStroke = new Point[STROKESIZE];
        baseSet = new Point[NUMSTROKES][STROKESIZE];

        try {
            FileReader myReader = new FileReader("strokedata.txt");
            BufferedReader myBufferedReader = new BufferedReader(myReader);
            for (stroke = 0; stroke < NUMSTROKES; stroke++)
                for (pointNum = 0; pointNum < STROKESIZE; pointNum++) {
                    inputLine = myBufferedReader.readLine();
                    x = Integer.parseInt(inputLine);
                    inputLine = myBufferedReader.readLine();
                    y = Integer.parseInt(inputLine);
                    baseSet[stroke][pointNum] = new Point(x, y);
                }
            myBufferedReader.close();
            myReader.close();
        }
        catch (IOException e) {
            System.out.println("Error writing to file.\n");
        }
    }
    
    
    public int findMaxX() 
    {
         int max = userStroke[0].x;
         for (int i = 0; i < nextFree; i++)
        {
            if (userStroke[i].x > max) max = userStroke[i].x;
        }
    return max;
    /* variable max hold the first value and run the for loop through the remaining
     * elements to found the largest x by comparing and assigning new higher 
     * x value to max when found then return max    
     */
    }
    public int findMaxY()// method to return the max of y of the point in the userStroke array
    {
         int max = userStroke[0].y;
         for (int i = 0; i < nextFree; i++)
        {
            if (userStroke[i].y > max) max = userStroke[i].y;
        }
    return max;
    }
    /* variable max hold the first value and run the for loop through the remaining
     * elements to found the largest y by comparing and assigning new higher 
     * y value to max when found then return max    
     */
    public int findMinX() 
    {
         int min = userStroke[0].x;
         for (int i = 0; i < nextFree; i++)
        {
            if (userStroke[i].x < min) min = userStroke[i].x;
        }
    return min;
    }     
    /* variable min hold the first value and run the for loop through the remaining
     * elements to found the smallest x by comparing and assigning new smaller 
     * x value to min when found then return min    
     */
    public int findMinY()
    {
         int min = userStroke[0].y;
         for (int i = 0; i < nextFree; i++)
        {
            if (userStroke[i].y < min) min = userStroke[i].y;
        }
    return min;
    }     
    /* variable min hold the first value and run the for loop through the remaining
     * elements to found the smallest y by comparing and assigning new smaller 
     * y value to min when found then return min    
     */
    /**
     * translate - Translates the points in the userStroke array by sliding them
     * as far to the upper-left as possible. It does this by finding the minX
     * value and the minY value. Then each point (x, y) is replaced with the
     * point (x-minX, y-minY). Note: you can use the translate method of the
     * Point class
     */
    public void translate()
    {
        int minX = findMinX(); 
        int minY = findMinY(); 
        for (int i = 0; i < nextFree; i++)
        {  
            userStroke[i].x -= minX;
            userStroke[i].y -= minY;
        }
    }
    /* The method get the min value of x and y. Then the for loop will minus 
     * the current x and y position by minX and minY in turn to move all points 
     * in the userPoints to the top (minus minY) and left (minus minX)
     */
    public void scale(){
        int maxX = findMaxX(); // assign max of x position
        int maxY = findMaxX(); // assign max of y position
        int max; 
        // 2 lines the code below used to find the maximum value between maxX and maxY and assign to max variable
        if (maxX > maxY) max = maxX; 
        else max = maxY;
        double scaleFactor =  (250.00 * 250.00) / max; // calculate scaleFactor
        /* for loop below used to set all points to the position of x and y to
         * new positions to cover the canvas* */
        for (int i = 0; i < nextFree; i++)
        {
            double xPosition = userStroke[i].getX() * scaleFactor; // change to new x position
            double yPosition = userStroke[i].getY() * scaleFactor; // change to new y position
            userStroke[i].setLocation(xPosition, yPosition); // replace point to a new location
        }
    }
    /* The method compare max value of x and y to find the biggest one and divide the canvas size
     * by that value to find the ratio. Then use the for loop to go through every points with 
     * x and y position which then by multiply by ratio to change its position to cover the 
     * canvas as much as possible. At the end, call the setLocation method to to set the point to
     * new position.
     */
    /**
     * insertOnePoint - inserts a new point between the two points that are the
     * farthest apart in the userStroke array. There must be at least two points
     * in the array
     */
    private void insertOnePoint()
    {
        int maxPosition = 0, newX, newY, distance;
        // compute distance between point 0 and point 1
        int maxDistance = (int) userStroke[0].distance(userStroke[1]);
        // for loop below used to find maxDistance and maxPosition
        for (int i = 0; i < nextFree - 1; i++)
            {
                distance = (int) userStroke[i].distance(userStroke[i+1]); // find distance using distance() method
                if (distance > maxDistance)
                {
                    maxDistance = distance;
                    maxPosition = i;
                }
            }            
        // slide that are to the right of cell maxPosition right by one
        for (int i = nextFree; i > maxPosition + 1; i--)
            userStroke[i] = userStroke[i - 1];
        // Insert the average
        newX = (int) (userStroke[maxPosition].getX() + userStroke[maxPosition + 2]
                .getX()) / 2;
        newY = (int) (userStroke[maxPosition].getY() + userStroke[maxPosition + 2]
                .getY()) / 2;
        userStroke[maxPosition + 1] = new Point(newX, newY);

        nextFree++;
    }
    /* 
     * 
     */
    /**
     * normalizeNumPoints - Adds points to the userStroke by inserting points
     * repeatedly until there are STROKESIZE points in the stroke
     */
    public void normalizeNumPoints()
    {
        while (nextFree < STROKESIZE) {
            insertOnePoint();
        }
    }
    public int computeScore(int digitToCompare) 
    {
        int index = digitToCompare;
        int sum = 0;     
        /* the block of code below calculate the sum of distance between userStroke's position 
         * and baseSet's potition by using distance method and sum up*/
        for (int i = 0; i < nextFree; i++)
            sum += (int) userStroke[i].distance(baseSet[index][i]);
        return sum;
    }
    /* The method sum up the distance bewteen the point of array userStroke and
     * tbe  pattern array of the baset array by calling the distance method. 
     */
    /**
     * computeScore Computes and returns a "score" that is a measure of how
     * closely the normalized userStroke array matches a given pattern array in
     * the baseset array. The score is the sum of the distances between
     * corresponding points in the userStroke array and the pattern array.
     * 
     * @param digitToCompare
     *            The index of the pattern in the baseset with which to compute
     *            the score
     */
    /**
     * findMatch - Finds and returns the index (an int) of the base set pattern
     * which most closely matches the user stroke.
     */
    public int findMatch()
    {
         {
        // Process the user's stroke: 1) translate, 2) scale, 3) normalize
        translate();
        if (nextFree >= 2) // check condition
        {
            scale();
        }
        normalizeNumPoints();
        int index = 0;
        int score = computeScore(0);
        // Compare the resulting userStroke array with each array in the baseset array
        // The for loop below used to find the indext that most closly matches
        for (int i = 0; i < NUMSTROKES; i++)
        {
            if (computeScore(i) < score)
            {
                score = computeScore(i);
                index = i;
            }
        }

        return index; 
    }
    /* After translating and scaling the input drawing, calling the normalizeNumPoints method 
     * the method find the lowest score using the for loop to go thorugh every elements 
     * and then return the index of the base set pattern that closest to the userStroke point.     * 
     */
    }    
    public void resetUserStroke()
    {
        nextFree = 0;
    }
    // Returns the number of points currently in the user stroke array.
    public int numUserPoints()
    {
        return nextFree;
    }

    // This returns the x portion of the i'th point in the user array
    public int getUserPointX(int i)
    {
        if ((i >= 0) && (i < nextFree))
            return ((int) userStroke[i].getX());
        else {
            System.out.println("Invalid value of i in getUserPoint");
            return (0);
        }
    }

    // This returns the y portion of the i'th point in the user array
    public int getUserPointY(int i)
    {
        if ((i >= 0) && (i < nextFree))
            return ((int) userStroke[i].getY());
        else {
            System.out.println("Invalid value of i in getUserPoint");
            return (0);
        }
    }

    public void addUserPoint(Point newPoint)
    {
        if (nextFree < STROKESIZE) {
            userStroke[nextFree] = newPoint;
            nextFree++;
        }
    }
}
