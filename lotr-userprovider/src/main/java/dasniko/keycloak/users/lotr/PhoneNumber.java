package dasniko.keycloak.users.lotr;

public record PhoneNumber(
	String value
) {
	public PhoneNumber {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("Phone number must not be null or empty");
		}
	}
}
