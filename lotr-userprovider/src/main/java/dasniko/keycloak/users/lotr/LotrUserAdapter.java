package dasniko.keycloak.users.lotr;

import de.keycloak.models.AbstractGroupModel;
import de.keycloak.models.AbstractRoleModel;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

@Slf4j
@Getter
public class LotrUserAdapter extends AbstractUserAdapterFederatedStorage {

	private LotrUser user;
	private boolean dirty;

	public LotrUserAdapter(
		KeycloakSession session,
		RealmModel realm,
		ComponentModel storageProviderModel,
		LotrUser user
	) {
		super(session, realm, storageProviderModel);
		this.storageId = new StorageId(storageProviderModel.getId(), user.getId().toString());
		this.user = user;
	}

	@Override
	public String getUsername() {
		return user.getPhone().value();
	}

	@Override
	public void setUsername(String username) {
		user = user.withPhone(new PhoneNumber(username));
		dirty = true;
	}

	@Override
	public void setCreatedTimestamp(Long timestamp) {
		user = user.withCreatedAt(Instant.ofEpochSecond(timestamp));
		dirty = true;
	}

	@Override
	public Long getCreatedTimestamp() {
		return user.getCreatedAt().getEpochSecond();
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		user = user.withEnabled(enabled);
		dirty = true;
	}

	@Override
	public String getEmail() {
		return null;
	}

	@Override
	public void setEmail(String email) {
		// no-op
	}

	@Override
	public boolean isEmailVerified() {
		return false;
	}

	@Override
	public void setEmailVerified(boolean verified) {
		// no-op
	}

	@Override
	public void setAttribute(String name, List<String> values) {
		String value = values != null && !values.isEmpty() ? values.getFirst() : null;
		if (name.equals(UserModel.USERNAME)) {
			setUsername(value);
			return;
		}
		super.setAttribute(name, values);
	}

	@Override
	public String getFirstAttribute(String name) {
		if (name.equals(UserModel.USERNAME)) {
			return getUsername();
		}
		return super.getFirstAttribute(name);
	}

	@Override
	public Stream<String> getAttributeStream(String name) {
		if (name.equals(UserModel.USERNAME)) {
			return Stream.of(getUsername());
		}
		return super.getAttributeStream(name);
	}


	@Override
	public Map<String, List<String>> getAttributes() {
		MultivaluedHashMap<String, String> attributes = getFederatedStorage().getAttributes(realm, this.getId());
		if (attributes == null) {
			attributes = new MultivaluedHashMap<>();
		}
		attributes.add(UserModel.USERNAME, getUsername());
		return attributes;
	}

	@Override
	protected Set<GroupModel> getGroupsInternal() {
        log.info("getGroupsInternal for user {}", user);
		return user.getGroups()
			.stream()
			.map(LotrUserAdapter::toGroupModel)
			.collect(Collectors.toSet());
	}

	@Override
	protected Set<RoleModel> getRoleMappingsInternal() {
        log.info("getRoleMappingsInternal for user {}", user);
		return user.getRoles()
			.stream()
			.map(this::toRoleModel)
			.collect(Collectors.toSet());
	}

	@Override
	public void setSingleAttribute(String name, String value) {
		if (name.equals(UserModel.USERNAME)) {
			setUsername(value);
			return;
		}
		super.setSingleAttribute(name, value);
	}

	private AbstractRoleModel toRoleModel(String roleName) {
		return new AbstractRoleModel(roleName, realm) {
		};
	}

	private static AbstractGroupModel toGroupModel(String groupName) {
		return new AbstractGroupModel(groupName) {
		};
	}
}
