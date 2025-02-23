package dasniko.keycloak.users.lotr;

import com.google.auto.service.AutoService;
import de.keycloak.util.BuildDetails;
import java.util.List;
import java.util.Map;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.provider.ServerInfoAwareProviderFactory;
import org.keycloak.storage.UserStorageProviderFactory;

@AutoService(UserStorageProviderFactory.class)
public class LotrUserStorageProviderFactory implements UserStorageProviderFactory<LotrUserStorageProvider>, ServerInfoAwareProviderFactory {

	public static final String PROVIDER_ID = "lotr-user-provider";

	@Override
	public LotrUserStorageProvider create(KeycloakSession session, ComponentModel model) {
		var usersProvider = new InMemLotrUserProvider();
		return new LotrUserStorageProvider(session, model, usersProvider);
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {
		// noop
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return ProviderConfigurationBuilder.create()
			.build();
	}

	@Override
	public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
		if (config.getId() == null) {
			config.setId(KeycloakModelUtils.generateShortId());
		}
	}

	@Override
	public void close() {
		// noop
	}

	@Override
	public Map<String, String> getOperationalInfo() {
		return BuildDetails.get();
	}
}
