package dasniko.keycloak.users.lotr;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface LotrUserProvider {

	Optional<LotrUser> getByPhone(PhoneNumber phoneNumber);

	Optional<LotrUser> getById(UUID id);

	Stream<LotrUser> getAll();
}
