package dasniko.keycloak.users.lotr;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public final class LotrUser {
	private final UUID id;
	@With
	private final PhoneNumber phone;
	@With
	private final boolean enabled;
	@With
	private final Instant createdAt;
	@With(value = AccessLevel.PRIVATE)
	private final Set<String> groups;
	@With(value = AccessLevel.PRIVATE)
	private final Set<String> roles;

	public LotrUser(
		UUID id,
		PhoneNumber phone,
		boolean enabled,
		Instant createdAt,
		Set<String> groups,
		Set<String> roles
	) {
		this.id = id;
		this.phone = phone;
		this.enabled = enabled;
		this.createdAt = createdAt == null ? Instant.now() : createdAt;
		this.groups = groups == null ? Set.of() : Set.copyOf(groups);
		this.roles = roles == null ? Set.of() : Set.copyOf(roles);
	}

	LotrUser joinGroup(String group) {
		var newGroups = new HashSet<>(groups);
		newGroups.add(group);
		return withGroups(Collections.unmodifiableSet(newGroups));
	}

	LotrUser leaveGroup(String group) {
		var newGroups = new HashSet<>(groups);
		newGroups.remove(group);
		return withGroups(Collections.unmodifiableSet(newGroups));
	}

	LotrUser addRole(String role) {
		var newRoles = new HashSet<>(roles);
		newRoles.add(role);
		return withRoles(Collections.unmodifiableSet(newRoles));
	}

	LotrUser removeRole(String role) {
		var newRoles = new HashSet<>(roles);
		newRoles.remove(role);
		return withRoles(Collections.unmodifiableSet(newRoles));
	}
}
