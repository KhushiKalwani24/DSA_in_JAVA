import java.util.*;
public class Prime_no {
    
    public static void main(String [] args){
        Scanner sc= new Scanner(System.in);
        int n=sc.nextInt();
        if(n==2){
            System.out.println(n + " is a prime no.");
        }
        else{
            boolean isPrime = true;
            for(int i=2; i<=Math.sqrt(n);i++)//for big numbers because sqrt n takes much less tome than n-2
            
            //for(int i=2; i<n; i++)
            {
                if(n%i==0){
                    isPrime=false;
                }
            }
            if(isPrime==true){
                System.out.println(n + " is a Prime no.");
            }
            else{
                System.out.println(n + " is not a prime no.");
            }
        }
        
    }
}
