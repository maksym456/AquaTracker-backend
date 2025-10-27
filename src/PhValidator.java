public class PhValidator {
    public boolean isSafePh(double ph) {   
        if (ph >= 6.5 && ph <= 7.5) {
            return true;  
        } else {
            return false;  
        }
    }
} 
//  takes a pH value, returns true if it's safe, false otherwise.
