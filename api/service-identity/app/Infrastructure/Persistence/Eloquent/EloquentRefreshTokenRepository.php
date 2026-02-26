<?php

namespace App\Infrastructure\Persistence\Eloquent;

use App\Domain\Auth\RefreshTokenRepositoryInterface;
use App\Domain\Auth\RefreshToken;
use DateTimeImmutable;

class EloquentRefreshTokenRepository implements RefreshTokenRepositoryInterface
{
    public function save(RefreshToken $refreshToken): void
    {
        RefreshTokenModel::updateOrCreate(
            ['id' => $refreshToken->getId()],
            [
                'user_id' => $refreshToken->getUserId(),
                'token_hash' => $refreshToken->getTokenHash(),
                'expires_at' => $refreshToken->getExpiresAt()->format('Y-m-d H:i:s'),
                'revoked_at' => $refreshToken->getRevokedAt()?->format('Y-m-d H:i:s'),
            ]
        );
    }

    public function findByTokenHash(string $tokenHash): ?RefreshToken
    {
        $model = RefreshTokenModel::where('token_hash', $tokenHash)->first();

        if (!$model) {
            return null;
        }

        return new RefreshToken(
            id: $model->id,
            userId: $model->user_id,
            tokenHash: $model->token_hash,
            expiresAt: new DateTimeImmutable($model->expires_at),
            revokedAt: $model->revoked_at ? new DateTimeImmutable($model->revoked_at) : null
        );
    }

    public function revokeAllForUser(int $userId): void
    {
        RefreshTokenModel::where('user_id', $userId)
            ->whereNull('revoked_at')
            ->update(['revoked_at' => now()]);
    }
}
