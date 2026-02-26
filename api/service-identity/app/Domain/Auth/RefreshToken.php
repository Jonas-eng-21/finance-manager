<?php

namespace App\Domain\Auth;

use DateTimeImmutable;

class RefreshToken
{
    public function __construct(
        private readonly string $id,
        private readonly int $userId,
        private readonly string $tokenHash,
        private readonly DateTimeImmutable $expiresAt,
        private ?DateTimeImmutable $revokedAt = null
    ) {}

    public function getId(): string
    {
        return $this->id;
    }

    public function getUserId(): int
    {
        return $this->userId;
    }

    public function getTokenHash(): string
    {
        return $this->tokenHash;
    }

    public function getExpiresAt(): DateTimeImmutable
    {
        return $this->expiresAt;
    }

    public function getRevokedAt(): ?DateTimeImmutable
    {
        return $this->revokedAt;
    }

    public function isExpired(): bool
    {
        $now = new DateTimeImmutable();
        return $this->expiresAt < $now;
    }

    public function isRevoked(): bool
    {
        return $this->revokedAt !== null;
    }

    public function isValid(): bool
    {
        return !$this->isExpired() && !$this->isRevoked();
    }

    public function revoke(): void
    {
        if (!$this->isRevoked()) {
            $this->revokedAt = new DateTimeImmutable();
        }
    }
}
