<?php

namespace App\Application\UseCases\User;

use App\Application\DTOs\User\UpdateUserDTO;
use App\Domain\User\UserRepositoryInterface;
use App\Domain\User\ValueObjects\UserName;
use App\Application\Contracts\AuditLoggerInterface;
use App\Domain\Audit\Enums\AuditAction;
use App\Domain\User\Exceptions\InvalidCurrentPasswordException;
use App\Domain\Auth\RefreshTokenRepositoryInterface;
use Exception;

class UpdateUserUseCase
{
    public function __construct(
        private readonly UserRepositoryInterface $userRepository,
        private readonly AuditLoggerInterface $auditLogger,
        private readonly RefreshTokenRepositoryInterface $refreshTokenRepository
    ) {}

    public function execute(UpdateUserDTO $dto): void
    {
        $user = $this->userRepository->findByEmail($dto->email);

        if (!$user) {
            throw new Exception("User not found.");
        }

        $isProfileUpdated = false;

        $currentName = is_string($user->getName()) ? $user->getName() : $user->getName()->getValue();

        if ($dto->name !== null && $dto->name !== $currentName) {
            $user->updateName(new UserName($dto->name));
            $isProfileUpdated = true;
        }

        if ($dto->newPassword !== null) {
            try {
                $user->updatePassword($dto->currentPassword, $dto->newPassword);
                $this->refreshTokenRepository->revokeAllForUser($user->getId());
                $this->auditLogger->log(AuditAction::PASSWORD_CHANGED, $user->getId());
            } catch (InvalidCurrentPasswordException $e) {
                throw new Exception('identity.user.errors.invalid_current_password');
            }
        }

        $this->userRepository->save($user);

        if ($isProfileUpdated) {
            $this->auditLogger->log(AuditAction::PROFILE_UPDATED, $user->getId());
        }
    }
}
