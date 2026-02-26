<?php

namespace Tests\Unit\Domain\Auth;

use App\Domain\Auth\RefreshToken;
use DateTimeImmutable;
use PHPUnit\Framework\TestCase;

class RefreshTokenTest extends TestCase
{
    public function test_it_creates_active_refresh_token(): void
    {
        $expiresAt = new DateTimeImmutable('+7 days');

        $token = new RefreshToken(
            id: 'uuid-123',
            userId: 1,
            tokenHash: 'hashed_string',
            expiresAt: $expiresAt
        );

        $this->assertFalse($token->isExpired());
        $this->assertFalse($token->isRevoked());
        $this->assertTrue($token->isValid());
    }

    public function test_it_can_be_revoked(): void
    {
        $token = new RefreshToken(
            id: 'uuid-123',
            userId: 1,
            tokenHash: 'hashed_string',
            expiresAt: new DateTimeImmutable('+7 days')
        );

        $token->revoke();

        $this->assertTrue($token->isRevoked());
        $this->assertFalse($token->isValid());
    }

    public function test_it_knows_when_it_is_expired(): void
    {
        $expiresAt = new DateTimeImmutable('-1 day');

        $token = new RefreshToken(
            id: 'uuid-123',
            userId: 1,
            tokenHash: 'hashed_string',
            expiresAt: $expiresAt
        );

        $this->assertTrue($token->isExpired());
        $this->assertFalse($token->isValid());
    }
}
