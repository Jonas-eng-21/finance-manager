<?php

namespace App\Domain\Auth;

interface RefreshTokenRepositoryInterface
{
    public function save(RefreshToken $refreshToken): void;

    public function findByTokenHash(string $tokenHash): ?RefreshToken;
    public function revokeAllForUser(int $userId): void;
}
