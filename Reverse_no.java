public class Reverse_no {
    public static void main (String [] args){
        int rev=0;
        int n=10899;
        while(n>0){
            int last_digit=n%10;
            rev=rev*10+last_digit;
            n=n/10;
        }
        System.out.println(rev);
    }
}
