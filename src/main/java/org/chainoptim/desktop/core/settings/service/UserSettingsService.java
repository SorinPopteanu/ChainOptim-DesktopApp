package org.chainoptim.desktop.core.settings.service;

import org.chainoptim.desktop.core.settings.dto.UpdateUserSettingsDTO;
import org.chainoptim.desktop.core.settings.model.UserSettings;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserSettingsService {

    CompletableFuture<Optional<UserSettings>> getUserSettings(String userId);

    CompletableFuture<Optional<UserSettings>> saveUserSettings(UpdateUserSettingsDTO userSettings);
}