package dasniko.keycloak.users.lotr;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserCountMethodsProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

@Slf4j
public class LotrUserStorageProvider implements UserStorageProvider,
	UserLookupProvider, UserQueryProvider, CredentialInputValidator, UserCountMethodsProvider {

	private final LotrUserProvider lotrUserProvider;
	private final KeycloakSession session;
	private final ComponentModel model;

	public LotrUserStorageProvider(
		KeycloakSession session,
		ComponentModel model,
		LotrUserProvider lotrUserProvider
	) {
		this.lotrUserProvider = lotrUserProvider;
		this.session = session;
		this.model = model;
	}

	@Override
	public void close() {
		// noop
	}

	@Override
	public boolean supportsCredentialType(String credentialType) {
		return PasswordCredentialModel.TYPE.equals(credentialType);
	}

	@Override
	public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
		return supportsCredentialType(credentialType);
	}

	@Override
	public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
		return supportsCredentialType(input.getType()) && input instanceof UserCredentialModel;
	}

	@Override
	public UserModel getUserById(RealmModel realm, String id) {
		log.info("getUserById: {}", id);
		return findUser(realm, UUID.fromString(StorageId.externalId(id)), lotrUserProvider::getById)
			.orElse(null);
	}

	@Override
	public UserModel getUserByUsername(RealmModel realm, String username) {
		log.info("getUserByUsername: {}", username);
		return findUser(realm, new PhoneNumber(username), lotrUserProvider::getByPhone)
			.orElse(null);
	}

	@Override
	public UserModel getUserByEmail(RealmModel realm, String email) {
		return null;
	}

	@Override
	public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
		return lotrUserProvider.getAll()
			.map(user -> new LotrUserAdapter(session, realm, model, user));
	}

	@Override
	public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
		return lotrUserProvider.getAll()
			.filter(user -> user.getGroups().contains(group.getName()))
			.map(user -> new LotrUserAdapter(session, realm, model, user));
	}

	@Override
	public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
		return lotrUserProvider.getAll()
			.map(user -> new LotrUserAdapter(session, realm, model, user));
	}

	@Override
	public int getUsersCount(RealmModel realm, boolean includeServiceAccount) {
		return (int) lotrUserProvider.getAll()
			.count();
	}

	private <I> Optional<UserModel> findUser(
		RealmModel realm,
		I identifier,
		Function<I, Optional<LotrUser>> fnFindUser
	) {
		var found = fnFindUser.apply(identifier);
		if (found.isEmpty()) {
			return Optional.empty();
		}
		var adapter = new LotrUserAdapter(session, realm, model, found.get());
		return Optional.of(adapter);
	}
}
