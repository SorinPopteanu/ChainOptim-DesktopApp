package org.chainoptim.desktop.core.user.service;

import org.chainoptim.desktop.core.user.dto.UserSearchResultDTO;
import org.chainoptim.desktop.core.user.model.User;
import org.chainoptim.desktop.shared.search.model.PaginatedResults;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    // Read
    CompletableFuture<Optional<User>> getUserByUsername(String username);
    CompletableFuture<Optional<List<User>>> getUsersByCustomRoleId(Integer customRoleId);
    CompletableFuture<Optional<PaginatedResults<UserSearchResultDTO>>> searchPublicUsers(String searchQuery, int page, int itemsPerPage);
    // Write
    CompletableFuture<Optional<User>> assignBasicRoleToUser(String userId, User.Role role);
    CompletableFuture<Optional<User>> assignCustomRoleToUser(String userId, Integer roleId);
    CompletableFuture<Optional<User>> removeUserFromOrganization(String userId, Integer organizationId);
}
