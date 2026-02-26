<?php

namespace App\Application\UseCases\Auth;

use App\Domain\Auth\RefreshTokenRepositoryInterface;

class LogoutUseCase
{
    public function __construct(
        private readonly RefreshTokenRepositoryInterface $refreshTokenRepository
    ) {}

    public function execute(int $userId): void
    {
        $this->refreshTokenRepository->revokeAllForUser($userId);
    }
}
