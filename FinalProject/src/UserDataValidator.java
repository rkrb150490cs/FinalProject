import java.util.regex.Pattern;

class PaternMatcher{
	public static boolean doesMatch(String regExpression, String string) {
		if (string == null)
			return false;
		if (string.equals(""))
			return false;
		if (regExpression == null)
			return true;
		Pattern pattern = Pattern.compile(regExpression);
		return pattern.matcher(string).matches();
	}
}

public class UserDataValidator {
	public static void validateName(String name) throws InvalidNameException{
		String regex = "^[a-zA-Z]+$";
	     if (!PaternMatcher.doesMatch(regex, name))
	    	 throw new InvalidNameException("Invalid name");
	}
	
	public static void validateName(Name name) throws InvalidNameException{
	     if (name == null)
	    	 throw new InvalidNameException("null Name value");
	}
	
	public static void validateEmail(String email) throws InvalidEmailException{
		String regex = "^[a-zA-Z0-9_+&*-]+"+"(?:\\.[a-zA-Z0-9_+&*-]+)*"+"@"+"(?:[a-zA-Z0-9-]+\\.)+"+"[a-zA-Z]{2,7}$";
	     if (!PaternMatcher.doesMatch(regex, email))
	    	 throw new InvalidEmailException(); 
	 }
	public static void validateUsername(String username) throws InvalidUsernameException{
		String regex = "^[a-zA-Z]+"+"[a-zA-Z0-9]*$";
		if (!PaternMatcher.doesMatch(regex, username))
	    	 throw new InvalidUsernameException(); 
	}
	public static void validatePassword(String password) throws InvalidPasswordException{
		String regex = null;
		if (!PaternMatcher.doesMatch(regex, password))
	    	 throw new InvalidPasswordException();
		
	}
}

@SuppressWarnings("serial")
abstract class InvalidUserException extends Exception{
	public InvalidUserException (String exceptionMessage) {
		super(exceptionMessage);
	}
}

@SuppressWarnings("serial")
class InvalidNameException extends InvalidUserException{
	public InvalidNameException (String message) {
		super(message);
	}
}

@SuppressWarnings("serial")
class InvalidEmailException extends InvalidUserException{
	public InvalidEmailException () {
		super("Invalid Email Address");
	}
}

@SuppressWarnings("serial")
class InvalidUsernameException extends InvalidUserException{
	public InvalidUsernameException () {
		super("Invalid Username");
	}
}

@SuppressWarnings("serial")
class InvalidPasswordException extends InvalidUserException{
	public InvalidPasswordException () {
		super("Invalid Password");
	}
}

@SuppressWarnings("serial")
class StateException extends Exception{
	public StateException(String message) {
		super(message);
	}
}

@SuppressWarnings("serial")
class AlreadyBlockedException extends StateException{
	public AlreadyBlockedException() {
		super("User is already blocked.");
	}
}

@SuppressWarnings("serial")
class AlreadyValidException extends StateException{
	public AlreadyValidException() {
		super("User is already valid/unblocked.");
	}
}


