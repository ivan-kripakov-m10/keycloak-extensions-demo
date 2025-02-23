package dasniko.keycloak.users.lotr;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemLotrUserProvider implements LotrUserProvider {

	private final Map<PhoneNumber, LotrUser> phoneIndex;
	private final Map<UUID, LotrUser> idIndex;

	public InMemLotrUserProvider() {
		var frodo = LotrUser.builder()
			.id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
			.phone(new PhoneNumber("+111111111111"))
			.roles(Set.of("user"))
			.enabled(true)
			.createdAt(Instant.now())
			.groups(Set.of("fellowship"))
			.build();
		var gandalf = LotrUser.builder()
			.id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
			.phone(new PhoneNumber("+222222222222"))
			.roles(Set.of("user", "admin"))
			.enabled(true)
			.createdAt(Instant.now())
			.groups(Set.of("fellowship"))
			.build();
		var sauron = LotrUser.builder()
			.id(UUID.fromString("33333333-3333-3333-3333-333333333333"))
			.phone(new PhoneNumber("+333333333333"))
			.roles(Set.of("dark-lord"))
			.enabled(true)
			.createdAt(Instant.now())
			.groups(Set.of("dark-lord-c-level"))
			.build();
		phoneIndex = Stream.of(frodo, gandalf, sauron)
			.collect(
				Collectors.toMap(LotrUser::getPhone, Function.identity())
			);
		idIndex = Stream.of(frodo, gandalf, sauron)
			.collect(
				Collectors.toMap(LotrUser::getId, Function.identity())
			);
	}

	@Override
	public Optional<LotrUser> getByPhone(PhoneNumber phoneNumber) {
		return Optional.ofNullable(phoneIndex.get(phoneNumber));
	}

	@Override
	public Optional<LotrUser> getById(UUID id) {
		return Optional.ofNullable(idIndex.get(id));
	}

	@Override
	public Stream<LotrUser> getAll() {
		return phoneIndex.values().stream();
	}
}
